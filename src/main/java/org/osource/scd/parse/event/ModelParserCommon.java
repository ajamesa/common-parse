package org.osource.scd.parse.event;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.holder.ReadHolder;
import com.alibaba.excel.util.ConverterUtils;
import org.osource.scd.param.ParseParam;
import org.osource.scd.utils.FileParseCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author chengdu
 * @date 2020/3/2
 */
public class ModelParserCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelParserCommon.class);

    public static <T> T convertCellDataMapToVo(Map<Integer, CellData> cellDataMap, AnalysisContext analysisContext,
                                               Class<T> clazz, ParseParam parseParam) {
        T t = null;
        try {
            t = (T) clazz.newInstance();
            ReadHolder currentReadHolder = analysisContext.currentReadHolder();
            Map<String, Method> fieldSetterMap = parseParam.getFieldSetterMap();
            for (Map.Entry<String, Method> entry : fieldSetterMap.entrySet()) {
                String columnChar = entry.getKey();
                Integer column = FileParseCommonUtil.EXCEL_COLUMN.get(columnChar);
                CellData cellData = cellDataMap.get(column);
                if (cellData == null) {
                    LOGGER.error("column char parse no data {}", columnChar);
                    continue;
                }
                String cellValue = (String) ConverterUtils.convertToJavaObject(cellData, null, null,
                        currentReadHolder.converterMap(),
                        currentReadHolder.globalConfiguration(), analysisContext.readRowHolder().getRowIndex(), column);
                if (parseParam.getCellFormat() != null) {
                    cellValue = parseParam.getCellFormat().format(columnChar, cellValue);
                }
                FileParseCommonUtil.invokeValue(t, entry.getValue(), cellValue);
            }
            if (parseParam.getBusinessDefineParse() != null) {
                parseParam.getBusinessDefineParse().defineParse(t, cellDataMap, parseParam);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }
}
