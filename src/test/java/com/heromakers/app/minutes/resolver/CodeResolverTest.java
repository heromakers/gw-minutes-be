package com.heromakers.app.minutes.resolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureGraphQlTester
class CodeResolverTest {
    @Autowired(required = true)
    private GraphQlTester graphQlTester;

    @DisplayName("2. 게시글 목록 가져오기")
    @Test
    void test_code(){
        String document = "query {\n" +
                "    codeList {\n" +
                "        codeId\n" +
                "        code\n" +
                "        codeLabel\n" +
                "    }\n" +
                "}";
        if(this.graphQlTester == null) System.out.println("this.graphQlTester is null");
//        this.graphQlTester.document(document) // .documentName("codeList")
//                .execute();
    }

    @Test
    void codeList() {
        String query = "{ codeList { codeId parentCode code codeLabel } }";
        graphQlTester.documentName("codeList")
                .execute();
//                .path("data.codeList")
//                .entityList(String.class)
//                .hasSize(2)
//                .contains("code1", "code2");
    }

}