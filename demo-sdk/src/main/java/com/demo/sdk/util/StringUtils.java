package com.demo.sdk.util;

import com.alibaba.fastjson.JSON;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

/**
 * @author caiLinFeng
 * @date 2018年1月30日
 */
public class StringUtils extends org.springframework.util.StringUtils {
    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

    public static void main(String[] args) {
        System.out.println(generateUUID());
        for (int i = 0; i < 10; i++) {
            System.out.println(getStringRandom(6));
        }
    }

    public static boolean isNotEmpty(Object str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * 生成 uuid
     *
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static String generateTraceId() {
        try {
            String traceId = TraceContext.traceId();
            if (isEmpty(traceId)) {
                return generateUUID();
            }
            //skyWalking traceId 第一个数是数字 当没有指定oap服务器时会是 ignore...
            int firstChar = traceId.charAt(0);
            if (firstChar < 48 || firstChar > 57 ) {
                return generateUUID();
            }

            return traceId;
        } catch (Exception e) {
            logger.error("skyWalking 生成traceId 错误", e);
            return generateUUID();
        }
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString();
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    /**
     * 向JSON文本中插入或更新数据并返回
     *
     * @param oldJson 原json字符串
     * @param newJson 需要新加或更新的json字符串
     * @author caiLinFeng
     * @date 2018年3月28日
     */
    @SuppressWarnings("unchecked")
    public static String jsonSet(String oldJson, String newJson) {
        if (oldJson == null) {
            return newJson;
        }
        if (newJson == null) {
            return oldJson;
        }
        Map<String, Object> mapSource = (Map<String, Object>) JSON.parse(oldJson);
        Map<String, Object> map = (Map<String, Object>) JSON.parse(newJson);
        for (Entry<String, Object> entry : map.entrySet()) {
            mapSource.put(entry.getKey(), entry.getValue());
        }
        return JSON.toJSONString(mapSource);
    }

    /**
     * 随机产生字符串
     *
     * @param length 字符串长度
     * @param type   类型 (0: 仅数字; 2:仅字符; 别的数字:数字和字符)
     * @return
     */
    public static String getRandomStr(int length, int type) {
        String str = "";
        int beginChar = 'a';
        int endChar = 'z';
        // 只有数字
        if (type == 0) {
            beginChar = 'z' + 1;
            endChar = 'z' + 10;
        }
        // 只有小写字母
        else if (type == 2) {
            beginChar = 'a';
            endChar = 'z';
        }
        // 有数字和字母
        else {
            beginChar = 'a';
            endChar = 'z' + 10;
        }

        // 生成随机类

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int tmp = (beginChar + random.nextInt(endChar - beginChar));
            // 大于'z'的是数字
            if (tmp > 'z') {
                tmp = '0' + (tmp - 'z');
            }
            str += (char) tmp;
        }

        return str;
    }

    /**
     * 生成随机数字和字母的字符串
     *
     * @param length 字符串长度
     * @return
     */
    public static String getStringRandom(int length) {
        StringBuilder val = new StringBuilder(length);
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }

    /**
     * 首字母小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return new StringBuilder().append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }

    }
}
