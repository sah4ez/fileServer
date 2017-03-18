package com.github.sah4ez.protocol;

import java.util.Map;

/**
 * Created by aleksandr on 18.03.17.
 */
public interface Command {
    String execute(Map<String, String> parameters) throws Exception;
}
