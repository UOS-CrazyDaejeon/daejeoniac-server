package com.daejeongwang.uoscrazydaejeon.config;

public final class SwaggerExamples {

    public static final String BAD_REQUEST = """
            {
              "success": false,
              "message": "IllegalArgumentException : 잘못된 요청입니다.",
              "code": 400
            }
            """;

    public static final String NOT_FOUND = """
            {
              "success": false,
              "message": "Resource not Found : 장소를 찾을 수 없습니다.",
              "code": 404
            }
            """;

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "message": "Internal server error",
              "code": 500
            }
            """;

    private SwaggerExamples() {
    }
}
