package org.openmhealth.data.generator.controller;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openmhealth.data.generator.Application;
import org.openmhealth.data.generator.configuration.DataGenerationSettings;
import org.openmhealth.data.generator.constant.MetricName;
import org.openmhealth.data.generator.converter.OffsetDateTime2String;
import org.openmhealth.data.generator.domain.*;
import org.openmhealth.data.generator.service.*;
import org.openmhealth.schema.domain.omh.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;
import static java.time.temporal.ChronoUnit.SECONDS;
/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-07 09:50
 * @description???controller for push data
 * get the generated data of this day from file, and load these data in lists
 * when the timeStamp is surpass by the current timeStamp, then push a bach of data.
 **/
@RestController("/pushData")
public class DataPushController {
    //Service
    private static final Logger log = LoggerFactory.getLogger(DataPushController.class);

    @Autowired
    private DataGenerationSettings dataGenerationSettings;

    @Autowired
    private List<DataGenerationSettings> dataGenerationSettingsList;

    @Autowired
    private Validator validator;

    @Autowired
    private List<DataPointGenerator> dataPointGenerators = new ArrayList<>();

    @Autowired
    private TimestampedValueGroupGenerationService valueGroupGenerationService;

    @Autowired
    private FileSystemDataPointWritingServiceImpl dataPointWritingService;

    private Map<String, DataPointGenerator<?>> dataPointGeneratorMap = new HashMap<>();



    @ApiOperation(value = "push???????????????pushGateway")
    @GetMapping("/pushOnce")
    public String pushData(){
        try {
            String url = "localhost:9091";
            CollectorRegistry registry = new CollectorRegistry();
            Gauge gauge = Gauge.build("my_custom_metric", "This is my custom metric.").create();
            gauge.set(23.12);
            gauge.register(registry);
            PushGateway pg = new PushGateway(url);
            Map<String, String> groupingKey = new HashMap<String, String>();
            groupingKey.put("instance", "my_instance");
            pg.pushAdd(registry, "my_job", groupingKey);
            return "push????????????";
        } catch (Exception e){
            e.printStackTrace();
        }
        return "push????????????";
    }

    @ApiOperation(value = "?????????")
    @GetMapping("/testHttpClient")
    public String httpPushData() throws IOException, JSONException {
        System.out.println("??????push??????...");
        setMeasureGenerationRequestDefaults();


        if (!areMeasureGenerationRequestsValid(dataGenerationSettings.getMeasureGenerationRequests())) {
            return "??????????????????";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = "";
        for (MeasureGenerationRequest request : dataGenerationSettings.getMeasureGenerationRequests()){
            Iterable<TimestampedValueGroup> valueGroups = valueGroupGenerationService.generateValueGroups(request);
            DataPointGenerator<?> dataPointGenerator = dataPointGeneratorMap.get(request.getGeneratorName());
            Iterable<? extends DataPoint<?>> dataPoints = dataPointGenerator.generateDataPoints(valueGroups);
            for (DataPoint dataPoint:dataPoints){
                jsonString = objectMapper.writeValueAsString(dataPoint);
                System.out.println("???????????????json?????????: "+jsonString);
            }
        }



        String respHtml = "";


        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/dataGenerator/forCallBack");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");//???????????????????????????????????????????????????
        //httpPost.setHeader("Accept", "*/*");?????????ok,?????????????????????????????????????????????json
        httpPost.setHeader("Accept", "application/json");                    //????????????????????????????????????????????????????????????
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        //System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
        System.out.println(statusCode);//200
        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity respEnt = response.getEntity();
            respHtml = EntityUtils.toString(respEnt, "UTF-8");
            System.out.println(respHtml);

            return "????????????";
        }
        System.out.println(respHtml);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resp",respHtml);
        return "????????????";
    }

    @ApiOperation(value = "?????????")
    @PostMapping("/forCallBack")
    public String callBackFor(HttpServletRequest request, @RequestBody DataPoint<?> data) throws JsonProcessingException {
        /*
         *  String data               ???????????????json??????????????????????????????data?????????
         * @RequestBody String data ?????????json????????????????????????json?????????
         * @RequestBody JSONObject jsonObject ????????????json?????????????????????JSONObject??????
         * */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String dataString = objectMapper.writeValueAsString(data);
        System.out.println("???????????????????????????:"+ dataString);
        return "????????????";
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
}
