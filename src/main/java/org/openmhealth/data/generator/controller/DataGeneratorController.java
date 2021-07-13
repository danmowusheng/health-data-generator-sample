package org.openmhealth.data.generator.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
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
import org.openmhealth.data.generator.dto.MeasureDTO;
import org.openmhealth.data.generator.service.*;
import org.openmhealth.data.generator.transfer.Transfer;
import org.openmhealth.schema.domain.omh.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * @program: data-generator
 * @author: LJ
 * @create: 2021-06-18 11:09
 **/
@RestController
@RequestMapping("/dataGenerator")
@Api(description = "提供给dataGenerator使用者的接口")
public class DataGeneratorController {
    //Service
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private DataGenerationSettings dataGenerationSettings;

    @Autowired
    private Validator validator;

    @Autowired
    private List<DataPointGenerator> dataPointGenerators = new ArrayList<>();

    @Autowired
    private List<Transfer> dataTransfer = new ArrayList<>();

    @Autowired
    private TimestampedValueGroupGenerationService valueGroupGenerationService;

    @Autowired
    private FileSystemDataPointWritingServiceImpl dataPointWritingService;

    @Autowired
    private DataWrite2FileService dataWrite2FileService;

    private Map<String, DataPointGenerator<?>> dataPointGeneratorMap = new HashMap<>();

    private Map<String, Transfer<?>> dataTransferMap = new HashMap<>();

    private OffsetDateTime2String offsetDateTime2String = new OffsetDateTime2String();

    private List<DataGenerationSettings> dataGenerationSettingsList = new ArrayList<>();


    @Autowired
    private ObjectMapper objectMapper;


    @PostConstruct
    public void initializeDataPointGenerationSettings(){
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
            //System.out.println(newSetting.toString());
        }
        System.out.println("initialization done");
    }


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
    * @Description: this initialized all dataPointGenerators, @PostConstruct is an annotation used to load this func when application is started
    * @Param: null
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @PostConstruct
    public void initializeDataPointGenerators() {
        System.out.println("初始化所有的dataGenerator...");

        for (DataPointGenerator generator : dataPointGenerators) {
            dataPointGeneratorMap.put(generator.getName(), generator);
            System.out.println(generator.getName());
        }
        System.out.println("共初始化"+dataPointGeneratorMap.size()+"个数据产生器");

        System.out.println("初始化所有需要的转换器...");
        for (Transfer transfer:dataTransfer){
            dataTransferMap.put(transfer.getName(), transfer);
            System.out.println(transfer.getName());
        }
        System.out.println("共初始化"+dataTransferMap.size()+"个转换器");
        System.out.println("初始化完成");
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
        if (!areMeasureGenerationRequestsValid(dataGenerationSetting.getMeasureGenerationRequests())) {
            return;
        }

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
        //dataPointWritingService.clearFile();
        //dataPointWritingService.setFilename(fileName);
        //dataPointWritingService.setAppend(true);
        //System.out.println("True fileName : "+ dataPointWritingService.getFilename());


        //为每一种数据类型建议一个文件夹表示其类型信息
        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()) {
            String name = request.getGeneratorName();
            //文件名
            String destination = fileName + name + ".json";
            System.out.println("存入文件:"+destination);

            System.out.println("dataTransfer Info: "+dataTransferMap.toString());
            System.out.println("当前数据产生器为: "+name);

            if(!dataTransferMap.keySet().contains(name))break;

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            System.out.println("对应转换器："+dataTransferMap.get(name));

            Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);

            DataPointGenerator<?> dataPointGenerator = dataPointGeneratorMap.get(name);
            /**
             * Iterable可以简单理解为实现了一个可以存放东西的容器
             * 里面可以存放各种各样的dataPoint
             * 从里面取数据是比较方便的
             * 这里"?"代表的是一种数据类型,如HeartRate
             */
            //Iterable<? extends DataPoint<?>> dataPoints = dataPointGenerator.generateDataPoints(valueGroups);
            //打印list信息
            String measureListString  = objectMapper.writeValueAsString(DTOlist);
            System.out.println(measureListString);

            jsonObject.setList(name, (List<? extends MeasureDTO>) DTOlist);
            System.out.println("当前Measure size:"+((List<? extends MeasureDTO>) DTOlist).size());
            for (MeasureDTO measureDTO:DTOlist){
                String jsonString = objectMapper.writeValueAsString(measureDTO);
                System.out.println("measure info:"+jsonString);
                //System.out.println(measureDTO.toString());
            }

            /**
             * 注释中是产生csv文件的代码
             String[] head = new String[]{dataPointGenerator.getName(), "timestamp"};
             List<List<Object>> values = new ArrayList<>();
             for(TimestampedValueGroup group: valueGroups){
             List<Object> row = new ArrayList<>();
             for(Map.Entry<String, BoundedRandomVariableTrend> trendEntry :request.getTrends().entrySet()){
             String key = trendEntry.getKey();
             System.out.println("key: "+key);
             if(key.equals("percentage")){
             row.add(group.getValue(key));
             }
             System.out.println("value: "+group.getValue(key));
             }
             row.add(group.getTimestamp());
             values.add(row);
             }
             createCSV(head, values, "OxygenSaturation.csv");
             **/
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
        System.out.println("jsonObject info:"+data);

        //test push data 代码
        String url = "http://192.168.0.129:8080/data";      //刑雄
        String url1 = "http://192.168.0.163:8080/data";    //文山
        pushTest(url, data);
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

    /**
    * @Description: get dataPoints from this application
    * @Param: DataGenerationRequest dataGenerationRequest
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @ApiOperation(value = "提交一次数据获取请求,获取一段时间内的dataPoints")
    @PostMapping("/getDataPoints")
    public List<DataPoint<?>> getDataPoints(@RequestBody MeasureRequest measureRequest) throws Exception {
        System.out.println("开始获取健康数据...");
        OffsetDateTime startTime = measureRequest.getStartTime();
        OffsetDateTime endTime = measureRequest.getEndTime();
        if (endTime==null){
            endTime = OffsetDateTime.now();
        }
        if(startTime.isAfter(endTime)){
            startTime = dataGenerationSettings.getStartDateTime();
            endTime = dataGenerationSettings.getEndDateTime();
        }
        System.out.println("开始时间为: "+startTime);
        System.out.println("结束时间为: "+endTime);
        //利用startTime以及endTime在文件中查找合适的健康数据
        //默认不超过一天,并且在同一天

        //1.load file
        String root = "data/";

        String fileName = root + offsetDateTime2String.convert(startTime) + ".json";

        try {
            File jsonFile = new File(fileName);
            if (!jsonFile.exists()){
                //这时候要么生成这一天的数据
                //要么报错给前端
                System.out.println("这天的数据不存在");
                return null;
            }
            FileReader fileReader = new FileReader(jsonFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonStr = "";
            List<DataPoint<?>> dataPointList = new ArrayList<>();
            long nums = 0;
            //2.json to dataPoint，并判断时间是否合适
            while ((jsonStr = reader.readLine()) != null) {
                nums++;
                //解析一行json数据，即一个dataPoint
                //这一段存在着异常反射
                String[] infos = jsonStr.split("#");
                Class HealthDataType = Class.forName(infos[0]);
                JavaType javaType = TypeFactory.defaultInstance().constructType(DataPoint.class, HealthDataType);
                DataPoint<?> dataPoint = objectMapper.readValue(infos[1], javaType);

                //用反射获取当前这个dataPoint的健康数据类型T的对象
                //getBody获取的是一个Map对象
                /**
                 *     "body": {
                 *         "effective_time_frame": {
                 *             "date_time": "2014-01-02T01:11:03Z"
                 *         },
                 *         "heart_rate": {
                 *             "unit": "beats/min",
                 *             "value": 99.8735279392298
                 *         }
                 *     }
                 */
                //{effective_time_frame={date_time=2018-01-06T11:47:50Z}, heart_rate={unit=beats/min, value=85.26091525246194}}


                /**
                 * 通过反射的一些尝试
                 * 不能直接通过dataPoint.getBody().getEffectiveTimeFrame()方法调用
                 * 我只需要调用getEffectiveTimeFrame()方法即可，想利用invoke来达成目的
                 * 但是不论如何，我总归绕不过去要处理dataPoint.getBody()这一步
                 *                 Method getEffective_time = HealthDataType.getMethod("getEffectiveTimeFrame");
                 *                 TimeFrame result = (TimeFrame)getEffective_time.invoke(dataPoint.getBody());
                 *                 以上方法调用失败，报错为:object is not an instance of declaring class
                 */
                HashMap TimeMap = (HashMap)dataPoint.getBody();
                if(isTimeStampValid(TimeMap,startTime,endTime)){
                    dataPointList.add(dataPoint);
                }
            }
            fileReader.close();
            reader.close();

            System.out.println("总共数据: "+nums+" 有效数据: "+dataPointList.size());
            //3.返回这个list
            return dataPointList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
    * @Description: 帮助检查当前dataPoint时间戳是否符合要求
    * @Param:
    * @author: LJ
    * @Date: 2021/7/2
    **/
    public boolean isTimeStampValid(HashMap map, OffsetDateTime startTime, OffsetDateTime endTime) throws JsonProcessingException {
        TimeFrame result = null;
        System.out.println("mapInfo:");
        for (Object key:map.keySet()) {
            if (key.equals("effective_time_frame")) {
                HashMap timeMap = (HashMap)map.get(key);

                //此时的字符串不是常规的json字符串，不带双引号
                //objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                String jsonString = objectMapper.writeValueAsString(timeMap);
                System.out.println(jsonString);

                result = objectMapper.readValue(jsonString, TimeFrame.class);
            }
        }


        /**疯狂嵌套的map尝试，不过思路太直了
         **/

        if(result.getTimeInterval()!=null){
            //endTime==null
            TimeInterval curTime = result.getTimeInterval();
            //System.out.println(curTime.getStartDateTime().toString());
            //System.out.println((curTime.getStartDateTime().plus(curTime.getDuration().getValue().longValue(), SECONDS)));
            if(curTime.getStartDateTime().isBefore(startTime)||
                    (curTime.getStartDateTime().plus(curTime.getDuration().getValue().longValue(), SECONDS)).isAfter(endTime)){
                return false;
            }
        }else {
            if(result.getDateTime().isBefore(startTime)||result.getDateTime().isAfter(endTime)){
                return false;
            }
        }
        System.out.println(result.getTimeInterval()==null?result.getDateTime().toString():result.getTimeInterval().getStartDateTime().toString());
        return true;
    }

    /**
    * @Description: reponse for a dataPoint request
    * @Param:
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @ApiOperation(value = "提交一次数据请求,获取实时dataPoint")
    @PostMapping("/getDataPoint")
    public String getDataPoint(){

        //以泛型存入，如何取出？
        //判断时间，取得对应的数据

        //1.load file?
        //2.从底部开始查找，json to dataPoint，找到一定时间范围内的所有健康数据？
        //3.返回这个list
        return "产生实时数据";
    }



    /**
    * @Description: This route use a measureGenerationRequests to generate a dataPoint
    * @Param: MeasureGenerationRequestList measureGenerationRequests
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @ApiOperation(value = "提交一次数据请求清单")
    @ApiImplicitParams({

    })
    @PostMapping("/generateData")
    public String generateData(@RequestBody DataGenerationRequest measureGenerationRequests) throws Exception {
        System.out.println("收到数据请求");

        if(measureGenerationRequests==null) setMeasureGenerationRequestDefaults();
        List<MeasureGenerationRequest> requests = measureGenerationRequests.getMeasureGenerationRequests();
        if (!areMeasureGenerationRequestsValid(requests)) {
            return "Request is inValid";
        }

        long totalWritten = 0;

        for (MeasureGenerationRequest request : measureGenerationRequests.getMeasureGenerationRequests()) {

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            DataPointGenerator<?> dataPointGenerator = dataPointGeneratorMap.get(request.getGeneratorName());

            Iterable<? extends DataPoint<?>> dataPoints = dataPointGenerator.generateDataPoints(valueGroups);

            long written = dataPointWritingService.writeDataPoints(dataPoints);
            totalWritten += written;

            log.info("The '{}' generator has written {} data point(s).", dataPointGenerator.getName(), written);
        }

        log.info("A total of {} data point(s) have been written.", totalWritten);
        return "Request is Valid, generates data:"+totalWritten;
    }


    private void setMeasureGenerationRequestDefaults() {

        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()) {

            if (request.getStartDateTime() == null) {
                request.setStartDateTime(dataGenerationSettings.getStartDateTime());
            }

            if (request.getEndDateTime() == null) {
                request.setEndDateTime(dataGenerationSettings.getEndDateTime());
            }

            if (request.getMeanInterPointDuration() == null) {
                request.setMeanInterPointDuration(dataGenerationSettings.getMeanInterPointDuration());
            }

            if (request.isSuppressNightTimeMeasures() == null) {
                request.setSuppressNightTimeMeasures(dataGenerationSettings.isSuppressNightTimeMeasures());
            }
        }
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
}
