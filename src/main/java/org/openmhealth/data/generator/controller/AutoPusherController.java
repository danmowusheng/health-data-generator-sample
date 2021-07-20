package org.openmhealth.data.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openmhealth.data.generator.configuration.DataGenerationSettings;
import org.openmhealth.data.generator.converter.OffsetDateTime2String;
import org.openmhealth.data.generator.domain.JsonObject;
import org.openmhealth.data.generator.dto.*;
import org.openmhealth.data.generator.transfer.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.*;

import java.time.OffsetDateTime;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmhealth.data.generator.controller.DataGeneratorController.dataCacheMap;
import static org.openmhealth.data.generator.controller.DataGeneratorController.generateDateTime;

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

    //用于获取带时间戳的一组数据

    //存储name->transfer的映射关系
    public static Map<String, Transfer<?>> dataTransferMap = new HashMap<>();
    @Autowired
    private OffsetDateTime2String offsetDateTime2String;

    //一组产生器的配置信息
    public static List<DataGenerationSettings> dataGenerationSettingsList = new ArrayList<>();

    //Jackson序列化工具
    @Autowired
    private ObjectMapper objectMapper;

    private static int SEQ = 0;


    /**
     * 3.编写push方法
     */
    @ApiOperation(value = "载入数据的测试方法")
    @GetMapping("/pushTest")
    public void pushData() throws IOException {
        //dateTime是当前时间，假设当前时间>generateDateTime，那么启动一次generate
        OffsetDateTime dateTime = OffsetDateTime.now(); //只需要初始化一次，之后全部由pusher自动化更新管理
        OffsetDateTime startTime = OffsetDateTime.now().plusSeconds(5);
        Integer timeWindow = 1;
        String root = "data/";
        //一直监听
        int count = 0;
        while(count==0){
            //模拟每一位用户push数据
            String date = offsetDateTime2String.convert(dateTime);

            /**
             * 载入内存,这个时候同时把数据载入缓存
             */
            List<JsonObject> userInfos = new ArrayList<>();
            for(DataGenerationSettings dataSettings:dataGenerationSettingsList){
                //1.从文件中载入数据至内存
                String userID = dataSettings.getUserID();
                String filePath = root + userID + "/" + date + "/";

                JsonObject jsonObject = loadData(filePath, userID, startTime, timeWindow);
                userInfos.add(jsonObject);
                //此时也将此条数据存入缓存
                Map<Integer, JsonObject> userCacheMap = dataCacheMap.get(userID);
                userCacheMap.put(SEQ, jsonObject);
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
            //设置下一次序号
            SEQ++;
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
        System.out.println("push data!!!!");
        System.out.println("the data has been pushed is :"+data);

        System.out.println("处理response...");
        CloseableHttpResponse response = httpclient.execute(httpPost);

    }

    public void pushData(String url, String data) throws IOException {
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
        System.out.println("push data!!!!");
        System.out.println("the data has been pushed is :"+data);

        System.out.println("处理response...");
        CloseableHttpResponse response = httpclient.execute(httpPost);
        //处理状态码
        int statusCode = response.getStatusLine().getStatusCode();
        //System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
        System.out.println(statusCode);//200

        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity respEnt = response.getEntity();
            String respStr = EntityUtils.toString(respEnt, "UTF-8");
            int respSeq = Integer.parseInt(respStr);
            System.out.println("返回序列号为:"+respSeq);
            String userID = "some-user";
            checkCache(respSeq, userID);
            Map<Integer, JsonObject> userCacheMap = dataCacheMap.get(userID);
            for(Map.Entry<Integer, JsonObject> cache:userCacheMap.entrySet()){
                //补发之前没有发出的
                //不仅要发出data，还有seq
            }
            System.out.println("返回值处理完成");
        }

        System.out.println("返回码状态有误，错误码为:"+statusCode);
    }

    public void checkCache(int respSeq, String userID){
        Map<Integer, JsonObject> userCacheMap = dataCacheMap.get(userID);
        if(userCacheMap.keySet().contains(respSeq)){
            //收到回复
            userCacheMap.remove(respSeq);
        }else {
            System.out.println("不存在这条缓存!");
        }
    }
}
