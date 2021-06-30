package org.openmhealth.data.generator.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import org.openmhealth.data.generator.Application;
import org.openmhealth.data.generator.configuration.DataGenerationSettings;
import org.openmhealth.data.generator.converter.OffsetDateTime2String;
import org.openmhealth.data.generator.domain.*;
import org.openmhealth.data.generator.service.*;
import org.openmhealth.schema.domain.omh.DataPoint;
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
    private TimestampedValueGroupGenerationService valueGroupGenerationService;

    @Autowired
    private FileSystemDataPointWritingServiceImpl dataPointWritingService;

    private Map<String, DataPointGenerator<?>> dataPointGeneratorMap = new HashMap<>();

    private OffsetDateTime2String offsetDateTime2String = new OffsetDateTime2String();

    /**
     * use hashMap to restore each dataPoint
     */
    private Map<String, Iterable<? extends DataPoint<?>>> dataPointMap = new HashMap<>();

    /**
    * @Description: this initialized all dataPointGenerators, @PostConstruct is an annotation used to load this func when application is started
    * @Param: null
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @PostConstruct
    public void initializeDataPointGenerators() {
        System.out.println("Start initial all dataPointGenerators...");
        for (DataPointGenerator generator : dataPointGenerators) {
            dataPointGeneratorMap.put(generator.getName(), generator);
            System.out.println(generator.getName());
        }

        System.out.println("initialization done");
    }

    /**
    * @Description: 需要一个常驻方法，一直生成健康数据并存入文件中，并且可以根据需要进行拓展，按照日期存储文件
    * @Param:
    * @author: LJ
    * @Date: 2021/6/23
    **/
    @ApiOperation(value = "提交一次数据请求,生成从当前时间或指定时间开始的一天健康数据")
    @GetMapping("/generateDataPoints")
    public void generateDataPoints() throws Exception{
        setMeasureGenerationRequestDefaults();
        OffsetDateTime startTime  = dataGenerationSettings.getStartDateTime();

        if (!areMeasureGenerationRequestsValid(dataGenerationSettings.getMeasureGenerationRequests())) {
            return;
        }

        long totalWritten = 0;
        String root = "data/";

        String fileName = root + offsetDateTime2String.convert(startTime) + ".json";
        System.out.println("fileName is: "+ fileName);

        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()) {

            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            DataPointGenerator<?> dataPointGenerator = dataPointGeneratorMap.get(request.getGeneratorName());
            /**
             * Iterable可以简单理解为实现了一个可以存放东西的容器
             * 里面可以存放各种各样的dataPoint
             * 从里面取数据是比较方便的
             * 这里"?"代表的是一种数据类型,如HeartRate
             */
            Iterable<? extends DataPoint<?>> dataPoints = dataPointGenerator.generateDataPoints(valueGroups);

            String[] head = new String[]{dataPointGenerator.getName(), "timestamp"};
            List<List<Object>> values = new ArrayList<>();
            for(TimestampedValueGroup group: valueGroups){
                List<Object> row = new ArrayList<>();
                for(Map.Entry<String, BoundedRandomVariableTrend> trendEntry :request.getTrends().entrySet()){
                    String key = trendEntry.getKey();
                    System.out.println("key: "+key);
                    row.add(group.getValue(key));
                    System.out.println("value: "+group.getValue(key));
                }
                row.add(group.getTimestamp());
                values.add(row);
            }
            createCSV(head, values, "heartRate.csv");
            dataPointWritingService.setFilename(fileName);

            //System.out.println("True fileName : "+ dataPointWritingService.getFilename());
            long written = dataPointWritingService.writeDataPoints(dataPoints);
            totalWritten += written;

            log.info("The '{}' generator has written {} data point(s).", dataPointGenerator.getName(), written);
        }

        log.info("A total of {} data point(s) have been written.", totalWritten);
    }


    /**
    * @Description: generate data for this application
    * @Param: DataGenerationRequest dataGenerationRequest
    * @author: LJ
    * @Date: 2021/6/22
    **/
    @ApiOperation(value = "提交一次数据获取请求,生成一段时间内的dataPoints")
    @PostMapping("/getDataPoints")
    public List<DataPoint<?>> getDataPoints(@RequestBody MeasureRequest measureRequest) throws Exception {
        System.out.println("开始获取健康数据...");
        OffsetDateTime startTime = measureRequest.getStartTime();
        OffsetDateTime endTime = measureRequest.getEndTime();
        if (endTime.equals(null)){
            endTime = OffsetDateTime.now();
        }
        if(startTime.isAfter(endTime)){
            startTime = dataGenerationSettings.getStartDateTime();
            endTime = dataGenerationSettings.getEndDateTime();
        }
        //利用startTime以及endTime在文件中查找合适的健康数据
        //默认不超过一天,并且在同一天


        //1.load file
        String root = "data/";

        String fileName = root + offsetDateTime2String.convert(startTime) + ".json";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonStr = "";
            ObjectMapper objectMapper = new ObjectMapper();
            List<DataPoint<?>> dataPointList = new ArrayList<>();
            while ((jsonStr = reader.readLine()) != null) {
                //解析一行json数据，即一个dataPoint
                //这一段存在着异常反射
                DataPoint<?> dataPoint = objectMapper.readValue(jsonStr, DataPoint.class);
                dataPointList.add(dataPoint);
            }
            fileReader.close();
            reader.close();
            //Iterable<? extends DataPoint<?>> dataPoints = dataPointList;

            return dataPointList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //2.json to dataPoint，并判断时间是否合适

        //3.返回这个list
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
