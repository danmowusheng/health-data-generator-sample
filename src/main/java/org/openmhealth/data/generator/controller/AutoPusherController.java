package org.openmhealth.data.generator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openmhealth.data.generator.additionalMeasure.ECGDetail;
import org.openmhealth.data.generator.additionalMeasure.ECGRecord;
import org.openmhealth.data.generator.configuration.DataGenerationSettings;
import org.openmhealth.data.generator.converter.OffsetDateTime2String;
import org.openmhealth.data.generator.converter.StringToOffsetDateTimeConverter;
import org.openmhealth.data.generator.domain.JsonObject;
import org.openmhealth.data.generator.domain.MeasureGenerationRequest;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.*;
import org.openmhealth.data.generator.service.DataWrite2FileService;
import org.openmhealth.data.generator.service.TimestampedValueGroupGenerationService;
import org.openmhealth.data.generator.transfer.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.validation.Validator;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-13 15:21
 * @description：push data automatically
 **/
@RestController
@RequestMapping("/autoData")
@Api(description = "自动化上传数据的接口")
public class AutoPusherController {
    //日志生成
    private static final Logger log = LoggerFactory.getLogger(AutoPusherController.class);
    //产生器的配置信息
    //重点在于开始时间和结束时间的配置
    @Autowired
    private DataGenerationSettings dataGenerationSettings;
    @Autowired
    private Validator validator;
    //数据转换器，用于获取dto类型的数据
    @Autowired
    private List<Transfer> dataTransfer = new ArrayList<>();
    //用于获取带时间戳的一组数据
    @Autowired
    private TimestampedValueGroupGenerationService valueGroupGenerationService;
    //用于将数据写入文件
    @Autowired
    private DataWrite2FileService dataWrite2FileService;
    //存储name->transfer的映射关系
    public static Map<String, Transfer<?>> dataTransferMap = new HashMap<>();
    @Autowired
    private OffsetDateTime2String offsetDateTime2String;
    @Autowired
    private StringToOffsetDateTimeConverter stringToOffsetDateTimeConverter;
    //一组产生器的配置信息
    public static List<DataGenerationSettings> dataGenerationSettingsList = new ArrayList<>();
    //Jackson序列化工具
    @Autowired
    private ObjectMapper objectMapper;

    //用于控制自动化
    OffsetDateTime generateDateTime;

    /**
     * 1.初始化产生器信息以及对应的Map映射关系
     */
    @PostConstruct
    public void initializeDataPointGenerationSettings() throws IOException {
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

        OffsetDateTime now = OffsetDateTime.now();
        //初始时间设置为当天的早上8点
        generateDateTime = OffsetDateTime.of(now.getYear(),now.getMonthValue(), now.getDayOfMonth(),8,0,0,0,ZoneOffset.UTC);
        System.out.println("初始化当天信息并生成这一天的数据");
        System.out.println("generateDateTime: "+generateDateTime);
        //初始化当天数据以及超前一天数据
        //一定保证generateDateTime这天数据生成后，天数自增1
        generateData(generateDateTime);
        generateDateTime = generateDateTime.plusDays(1);
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
            //文件名
            String destination = timeFile + name + ".json";
            File leaf = new File(destination);
            if (!leaf.exists()){
                leaf.createNewFile();
            }
            System.out.println("存入文件:" + destination);
            System.out.println("dataTransfer Info: " + dataTransferMap.toString());

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
                //DTOlist = getLocations(geoPositionDTOS);
            }

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
     * 3.编写push方法
     */
    @ApiOperation(value = "载入数据的测试方法")
    @GetMapping("/pushTest")
    public void pushData() throws IOException {
        //dateTime是当前时间，假设当前时间>generateDateTime，那么启动一次generate
        OffsetDateTime dateTime = OffsetDateTime.now(); //只需要初始化一次，之后全部由pusher自动化更新管理
        OffsetDateTime startTime = OffsetDateTime.now().plusSeconds(5);
        Integer timeWindow = 30;
        String root = "data/";
        //一直监听
        int count = 0;
        while(count==0){
            if(OffsetDateTime.now().isAfter(generateDateTime)){
                //产生一次数据
                /**
                 generateData(generateDateTime);
                 generateDateTime.plusDays(1);
                 **/
            }
            //模拟每一位用户push数据
            String date = offsetDateTime2String.convert(dateTime);

            //载入内存
            List<JsonObject> userInfos = new ArrayList<>();
            for(DataGenerationSettings dataSettings:dataGenerationSettingsList){
                //1.从文件中载入数据至内存
                String userID = dataSettings.getUserID();
                String filePath = root + userID + "/" + date + "/";

                JsonObject jsonObject = loadData(filePath, userID, startTime, timeWindow);
                userInfos.add(jsonObject);
            }

            //在载入之前判断时间戳是否已经抵达
            System.out.println("空操作开始,等待push...");
            while(OffsetDateTime.now().isBefore(startTime)){
                //nop
            }
            System.out.println("空操作结束");
            /**
             * if(dateTime.isAfter(startTime)){
             *    //此时开始push
             *}
             **/
            System.out.println("载入数据时间戳已到，开始push");
            //Push前需要将这次的数据载入缓冲区(Map)存储，以便于下次重传
            for(JsonObject jsonObject : userInfos){
                String data = objectMapper.writeValueAsString(jsonObject);
                System.out.println(data);
                //test push data 代码
                String url = "http://192.168.0.129:8080/data";      //刑雄
                String url1 = "http://192.168.0.163:8080/data";     //文山
                //pushTest(url, data);

                //接收到Response用于清除缓存
            }
            //当前List内容清空释放
            userInfos.clear();
            //增加下一次的开始push时间
            startTime =  startTime.plusMinutes(timeWindow);
            System.out.println("下一次push时间："+startTime);
            count++;
        }
    }
    /**
    * @Description: 将数据从文件中筛选出来并载入一位用户需要上传的信息中，最后返回这一条信息用于上传
    * @Param: filePath, userID, dateTime,这里应该还有一个进行筛选的时间窗口大小
    * @author: LJ
    * @Date: 2021/7/14
    **/
    public JsonObject loadData(String filePath, String userID, OffsetDateTime startTime, int timeWindow) throws IOException {
        JsonObject jsonObject = new JsonObject(userID);
        File parent = new File(filePath);
        //暂时不使用文件过滤器
        String[] jsonFiles = parent.list();
        System.out.println("文件数量:"+jsonFiles.length);
        for(String jsonFile:jsonFiles){
            System.out.println(jsonFile);
            //拿到键的信息
            String name = jsonFile.substring(0, jsonFile.lastIndexOf('.'));
            String jsonPath = filePath + jsonFile;
            File data = new File(jsonPath);
            //获取对应转换器
            Transfer<? extends MeasureDTO> transfer = dataTransferMap.get(name);

            FileReader fileReader = new FileReader(data);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonStr = "";
            /**
             * 这里我使用了父类的数组存储子类的信息，list中存储的都是同一种类型,只能是一种子类
             */
            List<MeasureDTO> measureDTOS = new ArrayList<>();
            while ((jsonStr = reader.readLine()) != null) {
                System.out.println("jsonStr:"+jsonStr);
                MeasureDTO measureDTO = transfer.newMeasureDTOMapper(jsonStr);
                //这里的假设是我的数据都是按时间顺序进行存储的
                //如果当前是之前已经取出的数据，那么跳过
                if (measureDTO.getTimeStamp()<startTime.getLong(ChronoField.INSTANT_SECONDS))continue;
                //如果超过当前需要的时间，那么结束
                if(measureDTO.getTimeStamp()>(startTime.plusMinutes(timeWindow)).getLong(ChronoField.INSTANT_SECONDS))break;
                //符合要求,就将这个数据载入内存
                measureDTOS.add(measureDTO);
                System.out.println("有效载入数据:"+objectMapper.writeValueAsString(measureDTO));
            }
            //结束后获取完整list数据
            jsonObject.setList(name, measureDTOS);
            System.out.println("jsonObject info:"+jsonObject);
        }

        return jsonObject;
    }

    public List<EcgDetailDTO> getEcgDetail(EcgRecordDTO record){
        List<EcgDetailDTO> details = new ArrayList<>();
        Long identifier = record.getTimeStamp();
        Integer frequency = record.getSamplingFrequency();
        Integer spendTime = 30;
        int num = (int)Math.pow(spendTime,2)/frequency;


        //这里我必须自己使用一个抖动函数
        for(int i=0;i<num;i++){
            OffsetDateTime dateTime = getTimeFromTs(record.getTimeStamp());
            EcgDetailDTO detail = new EcgDetailDTO.Builder(record.getEcgRecord().doubleValue(), identifier)
                                                    .setEcgLead(record.getEcgLead())
                                                    .setTimestamp(dateTime.plusSeconds(i))
                                                    .build();
            details.add(detail);
        }

        return details;
    }

    /**
     *
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

    /**
     * @param url
     * @param data
     * @throws IOException
     */
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
}
