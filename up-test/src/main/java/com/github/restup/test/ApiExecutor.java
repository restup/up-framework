package com.github.restup.test;

public interface ApiExecutor {

    ApiResponse<String[]> execute(RpcApiTest settings);

}
