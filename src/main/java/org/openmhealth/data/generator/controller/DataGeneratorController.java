package org.openmhealth.data.generator.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openmhealth.data.generator.Application;
import org.openmhealth.data.generator.configuration.DataGenerationSettings;
import org.openmhealth.data.generator.converter.OffsetDateTime2String;
import org.openmhealth.data.generator.domain.*;
import org.openmhealth.data.generator.dto.*;
import org.openmhealth.data.generator.service.*;
import org.openmhealth.data.generator.transfer.BodyWeightDTOTransfer;
import org.openmhealth.data.generator.transfer.Transfer;
import org.openmhealth.schema.domain.omh.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;


import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.openmhealth.data.generator.controller.AutoPusherController.dataGenerationSettingsList;
import static org.openmhealth.data.generator.controller.AutoPusherController.dataTransferMap;

/**
 * @program: data-generator
 * @author: LJ
 * @create: 2021-06-18 11:09
 **/
@RestController
@EnableScheduling
@RequestMapping("/dataGenerator")
@Api(description = "提供给dataGenerator使用者的接口")
public class DataGeneratorController {
    //Service
    private static final Logger log = LoggerFactory.getLogger(DataGeneratorController.class);
    //产生器的配置信息
    //重点在于开始时间和结束时间的配置
    @Autowired
    private DataGenerationSettings dataGenerationSettings;

    @Autowired
    private Validator validator;

    //@Autowired
    //private List<DataPointGenerator> dataPointGenerators = new ArrayList<>();

    //数据转换器，用于获取dto类型的数据
    @Autowired
    private List<Transfer> dataTransfer = new ArrayList<>();

    @Autowired
    private TimestampedValueGroupGenerationService valueGroupGenerationService;

    @Autowired
    private FileSystemDataPointWritingServiceImpl dataPointWritingService;

    @Autowired
    private DataWrite2FileService dataWrite2FileService;

    private Map<String, DataPointGenerator<?>> dataPointGeneratorMap = new HashMap<>();

    private OffsetDateTime2String offsetDateTime2String = new OffsetDateTime2String();

    @Autowired
    private ObjectMapper objectMapper;

    //用于控制自动化
    public static OffsetDateTime generateDateTime;

    public static Map<String, Map<Integer, JsonObject>> dataCacheMap = new HashMap<>();


    /**
     * 1.初始化产生器信息以及对应的Map映射关系
     */
    @PostConstruct
    public void initializeDataPointGenerationSettings() throws IOException {
        //初始化转换器以及初始用户信息
        InitialTransfer();
        //初始化每个用户的cache
        InitialCache();

        OffsetDateTime now = OffsetDateTime.now();
        //初始时间设置为当天的早上8点
        generateDateTime = OffsetDateTime.of(now.getYear(),now.getMonthValue(), now.getDayOfMonth(),8,0,0,0,ZoneOffset.UTC);
        System.out.println("初始化当天信息并生成这一天的数据");
        System.out.println("generateDateTime: "+generateDateTime);

        //初始化当天数据以及超前一天数据
        //一定保证generateDateTime这天数据生成后，天数自增1
        generateData(generateDateTime);
        System.out.println("初始化当天信息后,generateDateTime: "+generateDateTime);

        System.out.println("等待定时任务启动");
    }

    /**
     * 初始化转换器以及初始用户信息
     */
    public void InitialTransfer(){
        System.out.println("初始化所有需要的转换器...");
        for (Transfer transfer:dataTransfer){
            dataTransferMap.put(transfer.getName(), transfer);
            System.out.println(transfer.getName());
        }
        System.out.println("共初始化"+dataTransferMap.size()+"个转换器");
        System.out.println("初始化完成");

        System.out.println("Start initial all dataPointGeneratorsSettings...");
        System.out.println(dataGenerationSettings.toString());
        int size = 10;
        while (size!=0){
            size--;
            String userID = getId(size);
            DataGenerationSettings newSetting = new DataGenerationSettings();
            newSetting.setUserID(userID);
            newSetting.setStartDateTime(dataGenerationSettings.getStartDateTime());
            newSetting.setEndDateTime(dataGenerationSettings.getEndDateTime());
            newSetting.setMeanInterPointDuration(dataGenerationSettings.getMeanInterPointDuration());
            newSetting.setMeasureGenerationRequests(dataGenerationSettings.getMeasureGenerationRequests());
            dataGenerationSettingsList.add(newSetting);
            System.out.println(userID+"的数据产生器设置初始化成功!"+" 开始时间:"+newSetting.getStartDateTime());
        }
        System.out.println("initialization done");
    }

    /**
     * 初始化cache
     */
    public void InitialCache(){
        System.out.println("初始化cache信息");
        for (DataGenerationSettings dataSettings: dataGenerationSettingsList){
            dataCacheMap.put(dataSettings.getUserID(), new HashMap<>());
        }
        System.out.println("初始化cache完成");
    }

    //注解@Scheduled: 允许定时
    //每天8点启动一次  "0 0 8 * * ?"       每5s一次  "0/5 * * * * ?"
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateTimer() throws IOException {
        System.out.println("定时任务启动中...");
        generateData(generateDateTime);
        generateDateTime = generateDateTime.plusDays(1);
        System.out.println("启动定时任务成功！下一次启动时间:"+generateDateTime);
    }

    /**
     * 根据编号n生成用户名
     * @param n
     * @return userID
     */
    public String getId(int n){
        StringBuilder sb = new StringBuilder();
        int len = 3-(""+n).length();
        while (len!=0){
            sb.append(0);
            len--;
        }
        sb.append(n);
        return sb.toString();
    }

    /**
     * 2.编写产生当天数据并持久化存储的方法
     * 最好能超前一天生成一批数据
     */
    public void generateData(OffsetDateTime dateTime) throws IOException {
        System.out.println("正在产生"+dateTime+"这一天的数据...");
        System.out.println("dataGenerationSettingsList 大小:"+dataGenerationSettingsList.size());

        for (DataGenerationSettings dataGenerationSettings:dataGenerationSettingsList){
            generateDataForEachSettings(dataGenerationSettings, dateTime);
            //产生超前数据
            OffsetDateTime nextDateTime = dateTime.plusDays(1);
            generateDataForEachSettings(dataGenerationSettings, nextDateTime);
        }
        System.out.println(dateTime+"这一天的数据产生完毕,generateDateTime的时间需要自增1");
    }


    /**
     * 为一位用户的数据配置信息产生数据
     * @param dataSettings
     * @param dateTime
     */
    public void generateDataForEachSettings(DataGenerationSettings dataSettings, OffsetDateTime dateTime) throws IOException {
        String current = offsetDateTime2String.convert(dateTime);
        String userPath = "data/"+dataSettings.getUserID() + "/";
        String timeFile = userPath + current + "/";
        System.out.println("userId:"+dataSettings.getUserID());
        //按理来说，此时是不存在这个日期的文件的
        File parent = new File(timeFile);
        if(!parent.exists()){
            parent.mkdir();
        }else{
            System.out.println("当前时间已经存在数据，不用再次生成文件!");
            return;
        }
        System.out.println("filePath is: "+ timeFile);
        /**
         * 为每一项指标生成对应的数据
         */
        long totalWritten = 0;

        OffsetDateTime startDateTime = OffsetDateTime.of(dateTime.getYear(),dateTime.getMonthValue(),dateTime.getDayOfMonth(),0,0,0,0,ZoneOffset.UTC);
        OffsetDateTime endDateTime = startDateTime.plusDays(1);
        System.out.println("startDateTime: "+startDateTime+" endDateTime: "+endDateTime);
        for (MeasureGenerationRequest request : dataSettings.getMeasureGenerationRequests()) {
            //更新时间信息
            request.setStartDateTime(startDateTime);
            request.setEndDateTime(endDateTime);
            String name = request.getGeneratorName();

            if (!dataTransferMap.keySet().contains(name)) break;

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            System.out.println("对应转换器：" + dataTransferMap.get(name));

            Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);
            //打印list信息
            String measureListString = objectMapper.writeValueAsString(DTOlist);
            System.out.println(measureListString);

            /**
             * 对ECG-Record需要特殊处理
             */
            if (name.equals("ECG-record")){
                System.out.println("当前数据类型需要特殊处理，会产生一组衍生值");
                List<EcgRecordDTO> records = (List<EcgRecordDTO>)DTOlist;
                List<EcgDetailDTO> details = new ArrayList<>();
                for(EcgRecordDTO record:records){
                    details.addAll(getEcgDetail(record));
                }
                String path = timeFile + "ECG-detail" + ".json";
                dataWrite2FileService.setFilename(path);
                dataWrite2FileService.setAppend(true);
                //写入数据
                dataWrite2FileService.writeDatas(details);
                System.out.println("detail信息产生并写入完成!");
            }else if (name.equals("geo-position")){
                List<GeoPositionDTO> geoPositionDTOS = (List<GeoPositionDTO>) DTOlist;
                DTOlist = getLocations(geoPositionDTOS);
                //修改文件名
                name = "geo-detail";
            }

            //文件名
            String destination = timeFile + name + ".json";
            File leaf = new File(destination);
            if (!leaf.exists()){
                leaf.createNewFile();
            }
            System.out.println("存入文件:" + destination);
            System.out.println("dataTransfer Info: " + dataTransferMap.toString());

            System.out.println("当前Measure size:" + ((List<? extends MeasureDTO>) DTOlist).size());
            for (MeasureDTO measureDTO : DTOlist) {
                String jsonString = objectMapper.writeValueAsString(measureDTO);
                System.out.println("measure info:" + jsonString);
                //System.out.println(measureDTO.toString());
            }

            dataWrite2FileService.setFilename(destination);
            dataWrite2FileService.setAppend(true);
            //写入数据
            long written = dataWrite2FileService.writeDatas(DTOlist);
            totalWritten += written;
            log.info("The '{}' transfer has written {} data point(s).", name, written);
        }

        log.info("A total of {} data point(s) have been written.", totalWritten);
    }

    /**
    * @Description: 需要一个常驻方法，一直生成健康数据并存入文件中，并且可以根据需要进行拓展，按照日期存储文件
    * @Param:
    * @author: LJ
    * @Date: 2021/6/23
    **/
    @ApiOperation(value = "提交一次数据请求,生成从当前时间或指定时间开始的一天健康数据")
    @GetMapping("/generateDataPoints")
    public void generateDataPoints() throws IOException{
        System.out.println("开始生成数据...");
        //为每一位用户产生数据
        for (DataGenerationSettings dataGenerationSetting: dataGenerationSettingsList){
            generateDataForSettings(dataGenerationSetting);
        }

    }
    /**
    * @Description: 为一种配置生成对应的健康数据
    * @Param: DataSettings
    * @author: LJ
    * @Date: 2021/7/12
    **/
    public void generateDataForSettings(DataGenerationSettings dataGenerationSetting) throws IOException {
        OffsetDateTime startTime  = dataGenerationSetting.getStartDateTime();
        System.out.println("userId:"+dataGenerationSetting.getUserID());
        //创建一个用于上传的实体类类型
        JsonObject jsonObject = new JsonObject(dataGenerationSetting.getUserID());

        setMeasureGenerationRequestDefaults(dataGenerationSetting);
        /**
        if (!areMeasureGenerationRequestsValid(dataGenerationSetting.getMeasureGenerationRequests())) {
            return;
        }
         **/

        long totalWritten = 0;
        String root = "data/"+dataGenerationSetting.getUserID()+"/";
        File parent = new File(root);
        if(!parent.exists()){
            parent.mkdir();
        }
        //文件操作
        String fileName = root + offsetDateTime2String.convert(startTime)+"/"; //+ ".json"
        File leaf = new File(fileName);
        if(!leaf.exists()){
            leaf.mkdir();
        }
        System.out.println("fileName is: "+ fileName);
        //System.out.println("True fileName : "+ dataPointWritingService.getFilename());

        //为每一种数据类型建议一个文件夹表示其类型信息
        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()) {
            String name = request.getGeneratorName();

            System.out.println("dataTransfer Info: "+dataTransferMap.toString());
            System.out.println("当前数据产生器为: "+name);

            if(!dataTransferMap.keySet().contains(name))break;

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            System.out.println("对应转换器："+dataTransferMap.get(name));

            Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);

            /**
             * 对ECG-Record需要特殊处理
             */
            if (name.equals("ECG-record")){
                System.out.println("当前数据类型需要特殊处理，会产生一组衍生值");
                List<EcgRecordDTO> records = (List<EcgRecordDTO>)DTOlist;
                List<EcgDetailDTO> details = new ArrayList<>();
                for(EcgRecordDTO record:records){
                    details.addAll(getEcgDetail(record));
                }
                for (EcgDetailDTO ecgDetailDTO:details){
                    String jsonString = objectMapper.writeValueAsString(ecgDetailDTO);
                    System.out.println("measure info:"+jsonString);
                }
                String ecgDetailName = "ECG-detail";
                String path = fileName + ecgDetailName + ".json";
                dataWrite2FileService.setFilename(path);
                dataWrite2FileService.setAppend(false);
                //dataWrite2FileService.clearFile();
                //写入数据
                //dataWrite2FileService.writeDatas(details);
                jsonObject.setList(ecgDetailName, details);
                //System.out.println("detail信息产生并写入完成!");
            }else if (name.equals("geo-position")){
                List<GeoPositionDTO> geoPositionDTOS = (List<GeoPositionDTO>) DTOlist;
                DTOlist = getLocations(geoPositionDTOS);
                //修改name信息
                name = "geo-detail";
            }
            //文件名
            String destination = fileName + name + ".json";
            System.out.println("存入文件:"+destination);

            //打印list信息
            String measureListString  = objectMapper.writeValueAsString(DTOlist);
            //System.out.println(measureListString);

            jsonObject.setList(name, (List<? extends MeasureDTO>) DTOlist);
            System.out.println("当前Measure size:"+((List<? extends MeasureDTO>) DTOlist).size());
            for (MeasureDTO measureDTO:DTOlist){
                String jsonString = objectMapper.writeValueAsString(measureDTO);
                System.out.println("measure info:"+jsonString);
            }

            dataWrite2FileService.setFilename(destination);
            dataWrite2FileService.setAppend(true);
            //long written = dataWrite2FileService.writeDatas(DTOlist);
            //long written = dataPointWritingService.writeDataPoints(dataPoints);
            //totalWritten += written;

            //log.info("The '{}' generator has written {} data point(s).", dataPointGenerator.getName(), written);
        }
        /**
         * 设置objectMapper的字段命名映射策略为小驼峰
         */
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        String data = objectMapper.writeValueAsString(jsonObject);
        //System.out.println("jsonObject info:"+data);

        //test push data 代码
        String url = "http://192.168.0.129:8080/data";      //刑雄
        String url1 = "http://192.168.0.163:8080/data";    //文山
        //pushTest(url, data);
        log.info("A total of {} data point(s) have been written.", totalWritten);
    }


    public void pushTest(String url, String data) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");//表示客户端发送给服务器端的数据格式
        //httpPost.setHeader("Accept", "*/*");这样也ok,只不过服务端返回的数据不一定为json
        httpPost.setHeader("Accept", "application/json");                    //表示服务端接口要返回给客户端的数据格式，
        StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);

        System.out.println("push data!!!!");
        System.out.println("the data has been pushed is :"+data);
    }


    @GetMapping("/test/transfer")
    public void testTransfer() throws IOException {
        String filePath = "data/000/2018-1-1/body-weight.json";
        BodyWeightDTOTransfer transfer = new BodyWeightDTOTransfer();
        List<BodyWeightDTO> measureDTOS = new ArrayList<>();
        File file = new File(filePath);

        BufferedReader reader = null;
        try (FileReader fileReader = new FileReader(file)) {
            reader = new BufferedReader(fileReader);
            String jsonStr = "";

            //测试Transfer反序列化json数据
            while ((jsonStr = reader.readLine()) != null) {
                System.out.print(jsonStr);
                MeasureDTO measureDTO = transfer.newMeasureDTOMapper(jsonStr);
                System.out.print("      "+measureDTO.getTimeStamp());
                System.out.println();
                measureDTOS.add(transfer.newMeasureDTOMapper(jsonStr));
            }
            System.out.println(measureDTOS);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
    }


    private void setMeasureGenerationRequestDefaults() {
        setMeasureGenerationRequestDefaults(dataGenerationSettings);
    }

    private void setMeasureGenerationRequestDefaults(DataGenerationSettings dataGenerationSetting) {

        for (MeasureGenerationRequest request : dataGenerationSetting.getMeasureGenerationRequests()) {

            if (request.getStartDateTime() == null) {
                request.setStartDateTime(dataGenerationSetting.getStartDateTime());
            }

            if (request.getEndDateTime() == null) {
                request.setEndDateTime(dataGenerationSetting.getEndDateTime());
            }

            if (request.getMeanInterPointDuration() == null) {
                request.setMeanInterPointDuration(dataGenerationSetting.getMeanInterPointDuration());
            }

            if (request.isSuppressNightTimeMeasures() == null) {
                request.setSuppressNightTimeMeasures(dataGenerationSetting.isSuppressNightTimeMeasures());
            }
        }
    }

    /**
     * @return true if the requests are valid, false otherwise
     */
    private boolean areMeasureGenerationRequestsValid(List<MeasureGenerationRequest> requests) {

        //List<MeasureGenerationRequest> requests = dataGenerationSettings.getMeasureGenerationRequests();
        Joiner joiner = Joiner.on(", ");

        for (int i = 0; i < requests.size(); i++) {
            MeasureGenerationRequest request = requests.get(i);

            Set<ConstraintViolation<MeasureGenerationRequest>> constraintViolations = validator.validate(request);

            if (!constraintViolations.isEmpty()) {
                log.error("The measure generation request with index {} is not valid.", i);
                log.error(request.toString());
                log.error(constraintViolations.toString());
                return false;
            }

            if (!dataPointGeneratorMap.containsKey(request.getGeneratorName())) {
                log.error("The data generator '{}' in request {} doesn't exist.", request.getGeneratorName(), i);
                log.error(request.toString());
                log.error("The allowed data generators are: {}", joiner.join(dataPointGeneratorMap.keySet()));
                return false;
            }

            DataPointGenerator<?> generator = dataPointGeneratorMap.get(request.getGeneratorName());

            Set<String> specifiedTrendKeys = request.getTrends().keySet();
            Set<String> requiredTrendKeys = generator.getRequiredValueGroupKeys();

            if (!specifiedTrendKeys.containsAll(requiredTrendKeys)) {
                log.error("Request {} for generator '{}' is missing required trend keys.", i, generator.getName());
                log.error("The generator requires the following missing keys: {}.",
                        joiner.join(Sets.difference(requiredTrendKeys, specifiedTrendKeys)));
                return false;
            }

            Set<String> supportedTrendKeys = generator.getSupportedValueGroupKeys();

            if (!supportedTrendKeys.containsAll(specifiedTrendKeys)) {
                log.warn("Request {} for generator '{}' specifies unsupported trend keys.", i, generator.getName());
                log.warn("The generator supports the following keys: {}.", joiner.join(supportedTrendKeys));
                log.warn("The following keys are being ignored: {}.",
                        joiner.join(Sets.difference(specifiedTrendKeys, supportedTrendKeys)));
            }
        }

        return true;
    }


    /**
    * @Description:创建仅含值与时间戳的csv文件
    * @Param:
    * @author: LJ
    * @Date: 2021/6/29
    **/
    public void createCSV(Object[] head, List<List<Object>> dataList, String fileName){
        String filePath = "data/";       //文件路径

        List<Object> headList = Arrays.asList(head);
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try{
            csvFile = new File(filePath + fileName);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);
            writeRow(headList, csvWtriter);
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
    * @Description: 写入一行数据
    * @Param: 
    * @author: LJ
    * @Date: 2021/6/29
    **/
     public static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
         StringBuffer sb = new StringBuffer();
         boolean isFirst = true;
         for (Object data : row) {
             if(isFirst){
                 sb.append(data);
                 isFirst = false;
             }else{
                 sb.append(",");
                 sb.append(data);
             }
         }
         csvWriter.write(sb.toString());
         csvWriter.newLine();
    }

    @GetMapping("/create/csv")
    public void createData(){
        setMeasureGenerationRequestDefaults();
        for (MeasureGenerationRequest request:dataGenerationSettings.getMeasureGenerationRequests()){
            if (request.getGeneratorName().equals("step-count")){
                String name = request.getGeneratorName();
                System.out.println("current name:"+name);
                Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
                Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);

                String[] head = new String[]{"dateTime", name};
                List<List<Object>> dataList = new ArrayList<>();
                List<StepsDTO> stepsDTOS = (List<StepsDTO>)DTOlist;
                for(TimestampedValueGroup valueGroup: valueGroups){
                    List<Object> row = new ArrayList<>();
                    String dateTime = offsetDateTime2String.convert(valueGroup.getTimestamp());
                    row.add(dateTime);
                    Integer stepCount = valueGroup.getValue("steps-per-minute").intValue();
                    row.add(stepCount);
                    dataList.add(row);
                    System.out.println(row);
                }
                createCSV(head, dataList, "stepCount.csv");
            }
        }
    }

    /**
     * 根据EcgRecord产生对应的EcgDetail信息
     * @param record
     * @return List<EcgDetailDTO> details
     */
    public List<EcgDetailDTO> getEcgDetail(EcgRecordDTO record){
        List<EcgDetailDTO> details = new ArrayList<>();
        Long identifier = record.getTimeStamp();
        Integer frequency = record.getSamplingFrequency();
        Integer spendTime = 30;
        int num = (int)Math.pow(spendTime,2)/frequency;
        final int normalRate = 80;
        int deviation = Math.abs(record.getEcgRecord()-normalRate)>1?Math.abs(record.getEcgRecord()-normalRate):1;
        Random prng = new SecureRandom();
        double sum = 0;
        //这里我必须自己使用一个抖动函数
        for(int i=0;i<num;i++){
            OffsetDateTime dateTime = getTimeFromTs(record.getTimeStamp());
            double val = prng.nextGaussian()*deviation + record.getEcgRecord();
            val = (double)Math.round(val*100)/100;
            sum += val;
            EcgDetailDTO detail = new EcgDetailDTO.Builder(val, identifier)
                    .setEcgLead(record.getEcgLead())
                    .setTimestamp(dateTime.plusSeconds(i))
                    .build();
            details.add(detail);
        }
        int avg = (int)sum/num;
        record.setEcgRecord(avg);
        return details;
    }

    /**
     *  根据地理位置信息产生分开的经度和纬度信息
     * @param geoPositionDTOS
     * @return locations
     */
    public List<LocationSampleDTO> getLocations(List<GeoPositionDTO> geoPositionDTOS){
        List<LocationSampleDTO> locations = new ArrayList<>();

        for(GeoPositionDTO geoPositionDTO:geoPositionDTOS){
            //获取日期
            OffsetDateTime dateTime = getTimeFromTs(geoPositionDTO.getTimeStamp());
            //生成对应的经纬度信息
            LocationSampleDTO location1 = new LocationSampleDTO.Builder(geoPositionDTO.getLatitude())
                    .setGpsType(1)
                    .setTimestamp(dateTime)
                    .build();
            LocationSampleDTO location2 = new LocationSampleDTO.Builder(geoPositionDTO.getLongitude())
                    .setGpsType(2)
                    .setTimestamp(dateTime)
                    .build();
            locations.add(location1);
            locations.add(location2);
        }

        return locations;
    }

    /**
     * 将timeStamp转成OffsetDateTime
     * @param timeStamp
     * @return dateTime
     */
    public OffsetDateTime getTimeFromTs(long timeStamp){
        LocalDateTime localDateTime = Instant.ofEpochSecond(timeStamp).atOffset(ZoneOffset.UTC).toLocalDateTime();
        OffsetDateTime dateTime = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        return dateTime;
    }

    @GetMapping("/clean")
    public void cleanTodayFile(){
        OffsetDateTime today = OffsetDateTime.now();
        String root = "data/";
        String str_today = offsetDateTime2String.convert(today);
        for (DataGenerationSettings dataSettings: dataGenerationSettingsList){
            String userID = dataSettings.getUserID();
            String filePath = root + userID + "/" + str_today;
            File dateFile = new File(filePath);
            System.out.println("扫描路径:"+filePath);
            if(dateFile.isDirectory()){
                System.out.println("当前路径有文件，需要删除");
                File[] files = dateFile.listFiles();
                for (File file:files){
                    System.out.println("当前文件"+file.getName()+"删除结果:"+file.delete());
                }
            }
            dateFile.delete();
        }
        System.out.println("删除过程已完毕!");
    }
}
