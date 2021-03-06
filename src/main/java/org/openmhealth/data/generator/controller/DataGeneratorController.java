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
@Api(description = "?????????dataGenerator??????????????????")
public class DataGeneratorController {
    //Service
    private static final Logger log = LoggerFactory.getLogger(DataGeneratorController.class);
    //????????????????????????
    //????????????????????????????????????????????????
    @Autowired
    private DataGenerationSettings dataGenerationSettings;

    @Autowired
    private Validator validator;

    //@Autowired
    //private List<DataPointGenerator> dataPointGenerators = new ArrayList<>();

    //??????????????????????????????dto???????????????
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

    //?????????????????????
    public static OffsetDateTime generateDateTime;

    public static Map<String, Map<Integer, JsonObject>> dataCacheMap = new HashMap<>();


    /**
     * 1.???????????????????????????????????????Map????????????
     */
    @PostConstruct
    public void initializeDataPointGenerationSettings() throws IOException {
        //??????????????????????????????????????????
        InitialTransfer();
        //????????????????????????cache
        InitialCache();

        OffsetDateTime now = OffsetDateTime.now();
        //????????????????????????????????????8???
        generateDateTime = OffsetDateTime.of(now.getYear(),now.getMonthValue(), now.getDayOfMonth(),8,0,0,0,ZoneOffset.UTC);
        System.out.println("????????????????????????????????????????????????");
        System.out.println("generateDateTime: "+generateDateTime);

        //?????????????????????????????????????????????
        //????????????generateDateTime????????????????????????????????????1
        generateData(generateDateTime);
        System.out.println("????????????????????????,generateDateTime: "+generateDateTime);

        System.out.println("????????????????????????");
    }

    /**
     * ??????????????????????????????????????????
     */
    public void InitialTransfer(){
        System.out.println("?????????????????????????????????...");
        for (Transfer transfer:dataTransfer){
            dataTransferMap.put(transfer.getName(), transfer);
            System.out.println(transfer.getName());
        }
        System.out.println("????????????"+dataTransferMap.size()+"????????????");
        System.out.println("???????????????");

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
            System.out.println(userID+"???????????????????????????????????????!"+" ????????????:"+newSetting.getStartDateTime());
        }
        System.out.println("initialization done");
    }

    /**
     * ?????????cache
     */
    public void InitialCache(){
        System.out.println("?????????cache??????");
        for (DataGenerationSettings dataSettings: dataGenerationSettingsList){
            dataCacheMap.put(dataSettings.getUserID(), new HashMap<>());
        }
        System.out.println("?????????cache??????");
    }

    //??????@Scheduled: ????????????
    //??????8???????????????  "0 0 8 * * ?"       ???5s??????  "0/5 * * * * ?"
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateTimer() throws IOException {
        System.out.println("?????????????????????...");
        generateData(generateDateTime);
        generateDateTime = generateDateTime.plusDays(1);
        System.out.println("????????????????????????????????????????????????:"+generateDateTime);
    }

    /**
     * ????????????n???????????????
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
     * 2.???????????????????????????????????????????????????
     * ???????????????????????????????????????
     */
    public void generateData(OffsetDateTime dateTime) throws IOException {
        System.out.println("????????????"+dateTime+"??????????????????...");
        System.out.println("dataGenerationSettingsList ??????:"+dataGenerationSettingsList.size());

        for (DataGenerationSettings dataGenerationSettings:dataGenerationSettingsList){
            generateDataForEachSettings(dataGenerationSettings, dateTime);
            //??????????????????
            OffsetDateTime nextDateTime = dateTime.plusDays(1);
            generateDataForEachSettings(dataGenerationSettings, nextDateTime);
        }
        System.out.println(dateTime+"??????????????????????????????,generateDateTime?????????????????????1");
    }


    /**
     * ????????????????????????????????????????????????
     * @param dataSettings
     * @param dateTime
     */
    public void generateDataForEachSettings(DataGenerationSettings dataSettings, OffsetDateTime dateTime) throws IOException {
        String current = offsetDateTime2String.convert(dateTime);
        String userPath = "data/"+dataSettings.getUserID() + "/";
        String timeFile = userPath + current + "/";
        System.out.println("userId:"+dataSettings.getUserID());
        //?????????????????????????????????????????????????????????
        File parent = new File(timeFile);
        if(!parent.exists()){
            parent.mkdir();
        }else{
            System.out.println("?????????????????????????????????????????????????????????!");
            return;
        }
        System.out.println("filePath is: "+ timeFile);
        /**
         * ???????????????????????????????????????
         */
        long totalWritten = 0;

        OffsetDateTime startDateTime = OffsetDateTime.of(dateTime.getYear(),dateTime.getMonthValue(),dateTime.getDayOfMonth(),0,0,0,0,ZoneOffset.UTC);
        OffsetDateTime endDateTime = startDateTime.plusDays(1);
        System.out.println("startDateTime: "+startDateTime+" endDateTime: "+endDateTime);
        for (MeasureGenerationRequest request : dataSettings.getMeasureGenerationRequests()) {
            //??????????????????
            request.setStartDateTime(startDateTime);
            request.setEndDateTime(endDateTime);
            String name = request.getGeneratorName();

            if (!dataTransferMap.keySet().contains(name)) break;

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            System.out.println("??????????????????" + dataTransferMap.get(name));

            Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);
            //??????list??????
            String measureListString = objectMapper.writeValueAsString(DTOlist);
            System.out.println(measureListString);

            /**
             * ???ECG-Record??????????????????
             */
            if (name.equals("ECG-record")){
                System.out.println("???????????????????????????????????????????????????????????????");
                List<EcgRecordDTO> records = (List<EcgRecordDTO>)DTOlist;
                List<EcgDetailDTO> details = new ArrayList<>();
                for(EcgRecordDTO record:records){
                    details.addAll(getEcgDetail(record));
                }
                String path = timeFile + "ECG-detail" + ".json";
                dataWrite2FileService.setFilename(path);
                dataWrite2FileService.setAppend(true);
                //????????????
                dataWrite2FileService.writeDatas(details);
                System.out.println("detail???????????????????????????!");
            }else if (name.equals("geo-position")){
                List<GeoPositionDTO> geoPositionDTOS = (List<GeoPositionDTO>) DTOlist;
                DTOlist = getLocations(geoPositionDTOS);
                //???????????????
                name = "geo-detail";
            }

            //?????????
            String destination = timeFile + name + ".json";
            File leaf = new File(destination);
            if (!leaf.exists()){
                leaf.createNewFile();
            }
            System.out.println("????????????:" + destination);
            System.out.println("dataTransfer Info: " + dataTransferMap.toString());

            System.out.println("??????Measure size:" + ((List<? extends MeasureDTO>) DTOlist).size());
            for (MeasureDTO measureDTO : DTOlist) {
                String jsonString = objectMapper.writeValueAsString(measureDTO);
                System.out.println("measure info:" + jsonString);
                //System.out.println(measureDTO.toString());
            }

            dataWrite2FileService.setFilename(destination);
            dataWrite2FileService.setAppend(true);
            //????????????
            long written = dataWrite2FileService.writeDatas(DTOlist);
            totalWritten += written;
            log.info("The '{}' transfer has written {} data point(s).", name, written);
        }

        log.info("A total of {} data point(s) have been written.", totalWritten);
    }

    /**
    * @Description: ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    * @Param:
    * @author: LJ
    * @Date: 2021/6/23
    **/
    @ApiOperation(value = "????????????????????????,???????????????????????????????????????????????????????????????")
    @GetMapping("/generateDataPoints")
    public void generateDataPoints() throws IOException{
        System.out.println("??????????????????...");
        //??????????????????????????????
        for (DataGenerationSettings dataGenerationSetting: dataGenerationSettingsList){
            generateDataForSettings(dataGenerationSetting);
        }

    }
    /**
    * @Description: ??????????????????????????????????????????
    * @Param: DataSettings
    * @author: LJ
    * @Date: 2021/7/12
    **/
    public void generateDataForSettings(DataGenerationSettings dataGenerationSetting) throws IOException {
        OffsetDateTime startTime  = dataGenerationSetting.getStartDateTime();
        System.out.println("userId:"+dataGenerationSetting.getUserID());
        //??????????????????????????????????????????
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
        //????????????
        String fileName = root + offsetDateTime2String.convert(startTime)+"/"; //+ ".json"
        File leaf = new File(fileName);
        if(!leaf.exists()){
            leaf.mkdir();
        }
        System.out.println("fileName is: "+ fileName);
        //System.out.println("True fileName : "+ dataPointWritingService.getFilename());

        //??????????????????????????????????????????????????????????????????
        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()) {
            String name = request.getGeneratorName();

            System.out.println("dataTransfer Info: "+dataTransferMap.toString());
            System.out.println("????????????????????????: "+name);

            if(!dataTransferMap.keySet().contains(name))break;

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            System.out.println("??????????????????"+dataTransferMap.get(name));

            Iterable<? extends MeasureDTO> DTOlist = (dataTransferMap.get(name)).transferDatas(valueGroups);

            /**
             * ???ECG-Record??????????????????
             */
            if (name.equals("ECG-record")){
                System.out.println("???????????????????????????????????????????????????????????????");
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
                //????????????
                //dataWrite2FileService.writeDatas(details);
                jsonObject.setList(ecgDetailName, details);
                //System.out.println("detail???????????????????????????!");
            }else if (name.equals("geo-position")){
                List<GeoPositionDTO> geoPositionDTOS = (List<GeoPositionDTO>) DTOlist;
                DTOlist = getLocations(geoPositionDTOS);
                //??????name??????
                name = "geo-detail";
            }
            //?????????
            String destination = fileName + name + ".json";
            System.out.println("????????????:"+destination);

            //??????list??????
            String measureListString  = objectMapper.writeValueAsString(DTOlist);
            //System.out.println(measureListString);

            jsonObject.setList(name, (List<? extends MeasureDTO>) DTOlist);
            System.out.println("??????Measure size:"+((List<? extends MeasureDTO>) DTOlist).size());
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
         * ??????objectMapper???????????????????????????????????????
         */
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        String data = objectMapper.writeValueAsString(jsonObject);
        //System.out.println("jsonObject info:"+data);

        //test push data ??????
        String url = "http://192.168.0.129:8080/data";      //??????
        String url1 = "http://192.168.0.163:8080/data";    //??????
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
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");//???????????????????????????????????????????????????
        //httpPost.setHeader("Accept", "*/*");?????????ok,?????????????????????????????????????????????json
        httpPost.setHeader("Accept", "application/json");                    //????????????????????????????????????????????????????????????
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

            //??????Transfer????????????json??????
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
    * @Description:??????????????????????????????csv??????
    * @Param:
    * @author: LJ
    * @Date: 2021/6/29
    **/
    public void createCSV(Object[] head, List<List<Object>> dataList, String fileName){
        String filePath = "data/";       //????????????

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
    * @Description: ??????????????????
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
     * ??????EcgRecord???????????????EcgDetail??????
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
        //?????????????????????????????????????????????
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
     *  ????????????????????????????????????????????????????????????
     * @param geoPositionDTOS
     * @return locations
     */
    public List<LocationSampleDTO> getLocations(List<GeoPositionDTO> geoPositionDTOS){
        List<LocationSampleDTO> locations = new ArrayList<>();

        for(GeoPositionDTO geoPositionDTO:geoPositionDTOS){
            //????????????
            OffsetDateTime dateTime = getTimeFromTs(geoPositionDTO.getTimeStamp());
            //??????????????????????????????
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
     * ???timeStamp??????OffsetDateTime
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
            System.out.println("????????????:"+filePath);
            if(dateFile.isDirectory()){
                System.out.println("????????????????????????????????????");
                File[] files = dateFile.listFiles();
                for (File file:files){
                    System.out.println("????????????"+file.getName()+"????????????:"+file.delete());
                }
            }
            dateFile.delete();
        }
        System.out.println("?????????????????????!");
    }
}
