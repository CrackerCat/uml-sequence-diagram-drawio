package com.adrninistrator.usddi.common;

import java.math.BigDecimal;

/**
 * @author adrninistrator
 * @date 2021/9/15
 * @description:
 */
public class USDDIConstants {

    public static final String DESCRIPTION_FLAG = ">>";

    public static final String COMMENT_FLAG = "#";

    public static final String LIFELINE_TITLE_FLAG = "@";
    public static final String LIFELINE_ALIAS_FLAG = " as ";

    public static final String MESSAGE_REQ_FLAG = "=>";
    public static final String MESSAGE_RSP_FLAG = "<=";
    public static final String MESSAGE_ASYNC_FLAG = "->";
    public static final String MESSAGE_TEXT_FLAG = ":";

    public static final String LINK_FLAG = "~";

    public static final String CONF_DIR = "~usddi_conf";
    public static final String CONF_FILE_POSITION = "position.properties";
    public static final String CONF_FILE_STYLE = "style.properties";

    public static final String NEW_LINE = "\n";

    public static final String EXT_DRAWIO = ".drawio";

    public static final String HTML_NEW_LINE = "<br>";

    // 生命线中间点的水平间距
    public static final String KEY_LIFELINE_CENTER_HORIZONTAL_SPACING = "lifeline.center.horizontal.spacing";
    // 消息（及与生命线之间）垂直间距
    public static final String KEY_MESSAGE_VERTICAL_SPACING = "message.vertical.spacing";
    // 自调用消息的水平宽度
    public static final String KEY_SELF_CALL_HORIZONTAL_WIDTH = "self.call.horizontal.width";
    // 激活的宽度
    public static final String KEY_ACTIVATION_WIDTH = "activation.width";
    // 两个部分之间的额外垂直间距
    public static final String KEY_PARTS_EXTRA_VERTICAL_SPACING = "parts.extra.vertical.spacing";

    // 自动为消息添加序号
    public static final String KEY_MESSAGE_AUTO_SEQ = "message.auto.seq";
    // 线条宽度-生命线
    public static final String KEY_LINE_WIDTH_OF_LIFELINE = "line.width.of.lifeline";
    // 线条宽度-激活
    public static final String KEY_LINE_WIDTH_OF_ACTIVATION = "line.width.of.activation";
    // 线条宽度-消息
    public static final String KEY_LINE_WIDTH_OF_MESSAGE = "line.width.of.message";
    // 线条颜色-生命线
    public static final String KEY_LINE_COLOR_OF_LIFELINE = "line.color.of.lifeline";
    // 线条颜色-激活
    public static final String KEY_LINE_COLOR_OF_ACTIVATION = "line.color.of.activation";
    // 线条颜色-消息
    public static final String KEY_LINE_COLOR_OF_MESSAGE = "line.color.of.message";
    // 方框背景颜色-生命线
    public static final String KEY_BOX_COLOR_OF_LIFELINE = "box.color.of.lifeline";
    // 方框背景颜色-激活
    public static final String KEY_BOX_COLOR_OF_ACTIVATION = "box.color.of.activation";
    // 文字字体-生命线
    public static final String KEY_TEXT_FONT_OF_LIFELINE = "text.font.of.lifeline";
    // 文字字体-消息
    public static final String KEY_TEXT_FONT_OF_MESSAGE = "text.font.of.message";
    // 文字大小-生命线
    public static final String KEY_TEXT_SIZE_OF_LIFELINE = "text.size.of.lifeline";
    // 文字大小-消息
    public static final String KEY_TEXT_SIZE_OF_MESSAGE = "text.size.of.message";
    // 文字颜色-生命线
    public static final String KEY_TEXT_COLOR_OF_LIFELINE = "text.color.of.lifeline";
    // 文字颜色-消息
    public static final String KEY_TEXT_COLOR_OF_MESSAGE = "text.color.of.message";

    // 未指定链接时的描述与生命线的垂直间距
    public static final BigDecimal DESCRIPTION_WITHOUT_LINK_LIFELINE_VERTICAL_SPACING = BigDecimal.valueOf(60);

    // 指定链接时的描述与生命线的垂直间距
    public static final BigDecimal DESCRIPTION_WITH_LINK_LIFELINE_VERTICAL_SPACING = BigDecimal.valueOf(10);

    // 描述的高度
    public static final BigDecimal DESCRIPTION_HEIGHT = BigDecimal.valueOf(20);

    // 文字默认字体-宋体
    public static final String DEFAULT_FONT_NAME = "宋体";
    // 文字默认大小-12
    public static final int DEFAULT_FONT_SIZE = 12;
    // 文字默认字体-颜色
    public static final String DEFAULT_FONT_COLOR = "#000000";

    // 字体最小大小-12
    public static final int ALLOWED_MIN_FONT_SIZE = 12;

    // Lifeline方框宽度最大允许占用生命线中间点的水平间距的比例
    public static final BigDecimal LIFELINE_BOX_MAX_WIDTH_PERCENTAGE_LIFELINE = BigDecimal.valueOf(0.6D);
    // Lifeline方框宽度最大允许的固定值

    // Lifeline方框高度最小允许占用方框中文字实际高度的倍数
    public static final BigDecimal LIFELINE_BOX_MIN_HEIGHT_MULTIPLE_TEXT = BigDecimal.valueOf(2);
    // Lifeline方框高度最小允许的值，用于增加的值
    public static final BigDecimal LIFELINE_BOX_MIN_HEIGHT_ADD = BigDecimal.valueOf(30);

    // Lifeline方框宽度与高度的倍数
    public static final BigDecimal LIFELINE_BOX_WIDTH_HEIGHT_MULTIPLE = BigDecimal.valueOf(2);

    // Lifeline方框与文字的间距，横向
    public static final BigDecimal LIFELINE_BOX_BORDER_TEXT_SPACE_HORIZONTAL = BigDecimal.valueOf(4);

    // 消息宽度最大允许占用开始与结束的生命线之间水平间距的比例
    public static final BigDecimal MESSAGE_MAX_WIDTH_PERCENTAGE_LIFELINE = BigDecimal.valueOf(0.7D);
    // 消息宽度最大允许的固定值
    public static final BigDecimal MESSAGE_MAX_WIDTH_FIXED = BigDecimal.valueOf(400);

    private USDDIConstants() {
        throw new IllegalStateException("illegal");
    }
}
