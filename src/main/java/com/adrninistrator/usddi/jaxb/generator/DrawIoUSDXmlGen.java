package com.adrninistrator.usddi.jaxb.generator;

import com.adrninistrator.usddi.common.USDDIConstants;
import com.adrninistrator.usddi.conf.ConfPositionInfo;
import com.adrninistrator.usddi.conf.ConfStyleInfo;
import com.adrninistrator.usddi.dto.activation.ActivationInfo;
import com.adrninistrator.usddi.dto.description.DescriptionInfo;
import com.adrninistrator.usddi.dto.lifeline.LifelineInfo;
import com.adrninistrator.usddi.dto.message.MessageInfo;
import com.adrninistrator.usddi.dto.variables.UsedVariables;
import com.adrninistrator.usddi.jaxb.dto.MxArray;
import com.adrninistrator.usddi.jaxb.dto.MxCell;
import com.adrninistrator.usddi.jaxb.dto.MxGeometry;
import com.adrninistrator.usddi.jaxb.dto.MxGraphModel;
import com.adrninistrator.usddi.jaxb.dto.MxPoint;
import com.adrninistrator.usddi.jaxb.dto.MxRoot;
import com.adrninistrator.usddi.jaxb.dto.UserObject;
import com.adrninistrator.usddi.jaxb.util.JAXBUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author adrninistrator
 * @date 2021/9/21
 * @description: 生成draw.io格式的UML时序图类
 */
public class DrawIoUSDXmlGen {

    public static final String MX_AS_GEOMETRY = "geometry";
    public static final String MX_AS_SOURCE_POINT = "sourcePoint";
    public static final String MX_AS_TARGET_POINT = "targetPoint";
    public static final String MX_AS_POINTS = "points";

    private static final String ID_DESCRIPTION = "description-";
    private static final String ID_LIFELINE = "lifeline-";
    private static final String ID_ACTIVATION = "activation-";
    private static final String ID_MESSAGE = "message-";

    private final UsedVariables usedVariables;

    private final ConfPositionInfo confPositionInfo;
    private final ConfStyleInfo confStyleInfo;

    private final DescriptionInfo descriptionInfo;
    private final List<LifelineInfo> lifelineInfoList;
    private final Map<Integer, List<ActivationInfo>> activationMap;
    private final List<MessageInfo> messageInfoList;

    private String timestamp;

    private int elementId = 0;

    public DrawIoUSDXmlGen(UsedVariables usedVariables, ConfPositionInfo confPositionInfo, ConfStyleInfo confStyleInfo) {
        this.usedVariables = usedVariables;
        this.confPositionInfo = confPositionInfo;
        this.confStyleInfo = confStyleInfo;

        descriptionInfo = usedVariables.getDescriptionInfo();
        lifelineInfoList = usedVariables.getLifelineInfoList();
        activationMap = usedVariables.getActivationMap();
        messageInfoList = usedVariables.getMessageInfoList();
    }

    /**
     * 生成drawio的UML时序图XML文件
     *
     * @param xmlFilePath
     */
    public boolean generate(String xmlFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFilePath),
                StandardCharsets.UTF_8))) {
            if (lifelineInfoList.isEmpty()) {
                System.err.println("未指定生命线");
                return true;
            }

            if (messageInfoList.isEmpty()) {
                System.err.println("未指定消息");
                return true;
            }

            timestamp = String.valueOf(System.currentTimeMillis());

            // 生成基本数据
            MxGraphModel mxGraphModel = genBaseData();
            List<UserObject> userObjectList = mxGraphModel.getRoot().getUserObjectList();

            // 处理描述
            handleDescription(userObjectList);

            // 处理Lifeline
            handleLifeline(userObjectList);

            // 处理Activation
            handleActivation(userObjectList);

            // 处理Message
            handleMessage(userObjectList);

            // 生成XML文件
            JAXBUtil.javaBeanToXml(mxGraphModel, writer,
                    MxArray.class, MxCell.class, MxGeometry.class, MxGraphModel.class, MxPoint.class, MxRoot.class, UserObject.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 生成基本数据
    private MxGraphModel genBaseData() {
        MxGraphModel mxGraphModel = new MxGraphModel();
        mxGraphModel.setDx("0");
        mxGraphModel.setDy("0");
        mxGraphModel.setGrid("1");
        mxGraphModel.setGridSize("10");
        mxGraphModel.setGuides("1");
        mxGraphModel.setTooltips("1");
        mxGraphModel.setConnect("1");
        mxGraphModel.setArrows("1");
        mxGraphModel.setFold("1");
        mxGraphModel.setPage("1");
        mxGraphModel.setPageScale("1");
        mxGraphModel.setPageWidth("100");
        mxGraphModel.setPageHeight("100");
        mxGraphModel.setMath("0");
        mxGraphModel.setShadow("0");

        MxRoot root = new MxRoot();
        mxGraphModel.setRoot(root);

        // mxCell数量，等于Lifeline + Activation + 2
        int cellNum = lifelineInfoList.size();

        Collection<List<ActivationInfo>> activationInfoLists = activationMap.values();
        for (List<ActivationInfo> activationInfoList : activationInfoLists) {
            cellNum += activationInfoList.size();
        }
        cellNum += 2;

        List<MxCell> mxCellList = new ArrayList<>(cellNum);
        root.setMxCellList(mxCellList);

        MxCell mxCell0 = new MxCell();
        mxCell0.setId("0");

        MxCell mxCell1 = new MxCell();
        mxCell1.setId("1");
        mxCell1.setParent("0");

        mxCellList.add(mxCell0);
        mxCellList.add(mxCell1);

        // UserObject数量，等于Message总数量
        List<UserObject> userObjectList = new ArrayList<>(messageInfoList.size());
        root.setUserObjectList(userObjectList);

        return mxGraphModel;
    }

    // 处理描述
    private void handleDescription(List<UserObject> userObjectList) {
        if (!descriptionInfo.isUsed()) {
            return;
        }

        UserObject userObject = new UserObject();
        MxCell descriptionMxCell = new MxCell();
        userObject.setId(genElementId(ID_DESCRIPTION));

        userObject.setLabel(descriptionInfo.getDescription());
        if (descriptionInfo.getLink() != null) {
            userObject.setLink(descriptionInfo.getLink());
        }

        userObject.setMxCell(descriptionMxCell);
        descriptionMxCell.setVertex("1");
        descriptionMxCell.setParent("1");

        // 处理描述的样式
        String style = getDescriptionStyle();
        descriptionMxCell.setStyle(style);

        MxGeometry descriptionMxGeometry = new MxGeometry();
        descriptionMxCell.setMxGeometry(descriptionMxGeometry);
        descriptionMxGeometry.setAs(MX_AS_GEOMETRY);
        descriptionMxGeometry.setX("0");
        descriptionMxGeometry.setY("0");
        descriptionMxGeometry.setWidth(usedVariables.getTotalWidth().toPlainString());
        descriptionMxGeometry.setHeight(USDDIConstants.DESCRIPTION_HEIGHT.toPlainString());

        userObjectList.add(userObject);
    }

    // 处理Lifeline
    private void handleLifeline(List<UserObject> userObjectList) {
        for (LifelineInfo lifelineInfo : lifelineInfoList) {
            UserObject userObject = new UserObject();
            MxCell lifelineMxCell = new MxCell();
            userObject.setMxCell(lifelineMxCell);

            userObject.setId(genElementId(ID_LIFELINE));
            // 处理文字及字体
            String text = handleTextWithFont(lifelineInfo.getDisplayedName(),
                    confStyleInfo.getTextFontOfLifeline(), confStyleInfo.getTextSizeOfLifeline(), confStyleInfo.getTextColorOfLifeline());
            userObject.setLabel(text);

            userObject.setTooltip(lifelineInfo.getDisplayedName());

            // 处理Lifeline样式
            String style = getLifelineStyle();
            lifelineMxCell.setStyle(style);
            lifelineMxCell.setVertex("1");
            lifelineMxCell.setParent("1");

            MxGeometry lifelineMxGeometry = new MxGeometry();
            lifelineMxCell.setMxGeometry(lifelineMxGeometry);
            lifelineMxGeometry.setX(lifelineInfo.getCenterX().subtract(usedVariables.getLifelineBoxActualWidthHalf()).toPlainString());
            lifelineMxGeometry.setY(lifelineInfo.getStartY().toPlainString());
            lifelineMxGeometry.setWidth(usedVariables.getLifelineBoxActualWidth().toPlainString());
            lifelineMxGeometry.setHeight(usedVariables.getLifelineTotalHeight().toPlainString());
            lifelineMxGeometry.setAs(MX_AS_GEOMETRY);

            userObjectList.add(userObject);
        }
    }

    // 处理Activation
    private void handleActivation(List<UserObject> userObjectList) {
        int lifelineInfoListSize = lifelineInfoList.size();
        for (int i = 0; i < lifelineInfoListSize; i++) {
            LifelineInfo lifelineInfo = lifelineInfoList.get(i);
            List<ActivationInfo> activationInfoList = activationMap.get(i);
            if (activationInfoList == null) {
                // 当前Lifeline有可能没有Activation
                continue;
            }

            for (ActivationInfo activationInfo : activationInfoList) {
                UserObject userObject = new UserObject();
                MxCell activationMxCell = new MxCell();
                userObject.setMxCell(activationMxCell);

                userObject.setId(genElementId(ID_ACTIVATION));
                userObject.setLabel("");
                userObject.setTooltip(lifelineInfo.getDisplayedName());

                // 处理Activation样式
                String style = getActivationStyle();
                activationMxCell.setStyle(style);
                activationMxCell.setVertex("1");
                activationMxCell.setParent("1");

                MxGeometry activationMxGeometry = new MxGeometry();
                activationMxCell.setMxGeometry(activationMxGeometry);
                activationMxGeometry.setX(lifelineInfo.getCenterX().subtract(confPositionInfo.getActivationWidthHalf()).toPlainString());
                activationMxGeometry.setY(activationInfo.getTopY().toPlainString());
                activationMxGeometry.setWidth(confPositionInfo.getActivationWidth().toPlainString());
                activationMxGeometry.setHeight(activationInfo.getBottomY().subtract(activationInfo.getTopY()).toPlainString());
                activationMxGeometry.setAs(MX_AS_GEOMETRY);

                userObjectList.add(userObject);
            }
        }
    }

    // 处理Message
    private void handleMessage(List<UserObject> userObjectList) {
        for (MessageInfo messageInfo : messageInfoList) {
            UserObject userObject = new UserObject();
            MxCell messageMxCell = new MxCell();
            userObject.setId(genElementId(ID_MESSAGE));
            // 处理文字及字体
            String text = handleTextWithFont(messageInfo.getMessageText(),
                    confStyleInfo.getTextFontOfMessage(), confStyleInfo.getTextSizeOfMessage(), confStyleInfo.getTextColorOfMessage());
            userObject.setLabel(text);
            if (messageInfo.getLink() != null) {
                userObject.setLink(messageInfo.getLink());
            }
            userObject.setMxCell(messageMxCell);

            messageMxCell.setParent("1");
            messageMxCell.setEdge("1");

            MxGeometry messageMxGeometry = new MxGeometry();
            messageMxCell.setMxGeometry(messageMxGeometry);
            messageMxGeometry.setRelative("1");
            messageMxGeometry.setAs(MX_AS_GEOMETRY);

            List<MxPoint> mxPointList = new ArrayList<>(2);
            messageMxGeometry.setMxPointList(mxPointList);

            MxPoint sourcePoint = new MxPoint();
            sourcePoint.setX(messageInfo.getStartX().toPlainString());
            sourcePoint.setY(messageInfo.getMiddleY().toPlainString());
            sourcePoint.setAs(MX_AS_SOURCE_POINT);

            MxPoint targetPoint = new MxPoint();
            targetPoint.setX(messageInfo.getEndX().toPlainString());
            targetPoint.setY(messageInfo.getMiddleY().toPlainString());
            targetPoint.setAs(MX_AS_TARGET_POINT);

            mxPointList.add(sourcePoint);
            mxPointList.add(targetPoint);

            String style = null;
            switch (messageInfo.getMessageType()) {
                case MTE_REQ:
                    // 处理ReqMessage样式
                    style = getReqMessageStyle();
                    break;
                case MTE_RSP:
                    // 处理RspMessage样式
                    style = getRspMessageStyle();
                    break;
                case MTE_SELF:
                    // 处理SelfMessage样式
                    style = getSelfMessageStyle();

                    // 对于自调用消息，y坐标需要单独设置
                    sourcePoint.setY(messageInfo.getTopY().toPlainString());
                    targetPoint.setY(messageInfo.getBottomY().toPlainString());
                    // 对于自调用消息，起点x坐标对应左x坐标，终点x坐标对应右x坐标
                    targetPoint.setX(messageInfo.getStartX().toPlainString());

                    MxPoint point1 = new MxPoint();
                    point1.setX(messageInfo.getEndX().toPlainString());
                    point1.setY(messageInfo.getTopY().toPlainString());

                    MxPoint point2 = new MxPoint();
                    point2.setX(messageInfo.getEndX().toPlainString());
                    point2.setY(messageInfo.getBottomY().toPlainString());

                    List<MxPoint> mxPointList4Self = new ArrayList<>(2);
                    mxPointList4Self.add(point1);
                    mxPointList4Self.add(point2);

                    MxArray array = new MxArray();
                    array.setAs(MX_AS_POINTS);
                    array.setMxPointList(mxPointList4Self);

                    messageMxGeometry.setArray(array);
                    break;
                case MTE_ASYNC:
                    // 处理AsyncMessage样式
                    style = getAsyncMessageStyle();
                    break;
            }
            messageMxCell.setStyle(style);

            userObjectList.add(userObject);
        }
    }

    private String genElementId(String prefix) {
        return prefix + timestamp + "-" + (++elementId);
    }

    // 处理描述样式
    private String getDescriptionStyle() {
        Map<String, String> map = new HashMap<>();
        map.put("html", "1");
        map.put("strokeColor", "none");
        map.put("fillColor", "none");
        map.put("align", "left");
        map.put("verticalAlign", "bottom");
        map.put("whiteSpace", "wrap");
        map.put("rounded", "0");
        map.put("labelPosition", "center");
        map.put("verticalLabelPosition", "top");
        return "text;" + getStyleStringFromMap(map);
    }

    // 处理Lifeline样式
    private String getLifelineStyle() {
        Map<String, String> map = new HashMap<>();
        map.put("shape", "umlLifeline");
        map.put("perimeter", "lifelinePerimeter");
        map.put("whiteSpace", "wrap");
        map.put("html", "1");
        map.put("container", "1");
        map.put("collapsible", "0");
        map.put("recursiveResize", "0");
        map.put("outlineConnect", "0");
        map.put("size", usedVariables.getLifelineBoxActualHeight().toPlainString());
        if (confStyleInfo.getLineWidthOfLifeline() != null) {
            map.put("strokeWidth", confStyleInfo.getLineWidthOfLifeline().toPlainString());
        }
        if (StringUtils.isNotBlank(confStyleInfo.getLineColorOfLifeline())) {
            map.put("strokeColor", confStyleInfo.getLineColorOfLifeline());
        }
        if (StringUtils.isNotBlank(confStyleInfo.getBoxColorOfLifeline())) {
            map.put("fillColor", confStyleInfo.getBoxColorOfLifeline());
        }

        return getStyleStringFromMap(map);
    }

    // 处理Activation样式
    private String getActivationStyle() {
        Map<String, String> map = new HashMap<>();
        map.put("html", "1");
        map.put("points", "[]");
        map.put("perimeter", "orthogonalPerimeter");
        if (confStyleInfo.getLineWidthOfActivation() != null) {
            map.put("strokeWidth", confStyleInfo.getLineWidthOfActivation().toPlainString());
        }
        if (StringUtils.isNotBlank(confStyleInfo.getLineColorOfActivation())) {
            map.put("strokeColor", confStyleInfo.getLineColorOfActivation());
        }
        if (StringUtils.isNotBlank(confStyleInfo.getBoxColorOfActivation())) {
            map.put("fillColor", confStyleInfo.getBoxColorOfActivation());
        }

        return getStyleStringFromMap(map);
    }

    // 处理Message样式
    private Map<String, String> getMessageStyle() {
        Map<String, String> map = new HashMap<>();
        if (confStyleInfo.getLineWidthOfMessage() != null) {
            map.put("strokeWidth", confStyleInfo.getLineWidthOfMessage().toPlainString());
        }
        if (StringUtils.isNotBlank(confStyleInfo.getLineColorOfMessage())) {
            map.put("strokeColor", confStyleInfo.getLineColorOfMessage());
        }
        return map;
    }

    // 处理ReqMessage样式
    private String getReqMessageStyle() {
        Map<String, String> map = getMessageStyle();
        map.put("endArrow", "block");
        map.put("html", "1");
        map.put("endFill", "1");

        return getStyleStringFromMap(map);
    }

    // 处理RspMessage样式
    private String getRspMessageStyle() {
        Map<String, String> map = getMessageStyle();
        map.put("endArrow", "open");
        map.put("html", "1");
        map.put("dashed", "1");
        map.put("endFill", "0");

        return getStyleStringFromMap(map);
    }

    // 处理SelfMessage样式
    private String getSelfMessageStyle() {
        Map<String, String> map = getMessageStyle();
        map.put("endArrow", "block");
        map.put("html", "1");
        map.put("rounded", "0");
        map.put("endFill", "1");
        map.put("align", "left");
        map.put("labelBackgroundColor", "none");

        return getStyleStringFromMap(map);
    }

    // 处理AsyncMessage样式
    private String getAsyncMessageStyle() {
        Map<String, String> map = getMessageStyle();
        map.put("endArrow", "open");
        map.put("html", "1");
        map.put("endFill", "0");

        return getStyleStringFromMap(map);
    }

    private String getStyleStringFromMap(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(";");
        }
        return stringBuilder.toString();
    }

    // 处理文字及字体
    private String handleTextWithFont(String text, String textFont, Integer textSize, String textColor) {
        StringBuilder textStyle = new StringBuilder();
        if (StringUtils.isNotBlank(textFont)) {
            textStyle.append(" ").append("face=\"").append(textFont).append("\"");
        }
        if (textSize != null) {
            textStyle.append(" ").append("style=\"font-size: ").append(textSize).append("px\"");
        }
        if (StringUtils.isNotBlank(textColor)) {
            textStyle.append(" ").append("color=\"").append(textColor).append("\"");
        }
        if (textStyle.length() == 0) {
            return text;
        }

        return "<font" + textStyle + ">" + text + "</font>";
    }
}
