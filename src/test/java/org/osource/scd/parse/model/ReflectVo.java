package org.osource.scd.parse.model;

import lombok.Data;

import java.util.Date;

/**
 * @author chengdu
 *
 */
@Data
public class ReflectVo {

    private Integer id;

    private String userName;

    private Double score;

    private Long sort;

    private Date date;

    private String otherInfo;
}
