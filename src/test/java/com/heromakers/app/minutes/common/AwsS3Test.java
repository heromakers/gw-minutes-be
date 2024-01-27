package com.heromakers.app.minutes.common;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AwsS3Test {

    @Test
    void upload() {
        String base64Data = "iVBORw0KGgoAAAANSUhEUgAAADsAAAA7CAIAAABKR2XkAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAADwSURBVGhD7ZXLDcQgDESpi4Koh2pohmISm08EWXbRHkZxpHm3xEN4sjBxx9ugMR4a46ExHhrjoTEeGuOhMR4a46ExHhrjoTGevXEKbk1I67KPuSwUcorBt9fOh1QKmw/u+KfHOfpRp6L7r7fSuJR6PucU4hxcfnAHzljTm64ZM/7e/AtjxqXyW+g544khkevceTm/Sy1rPa5c10W/KgZsGlfknlDtW9CysaLJebl148/lxozl/MrA9bQc5/I/aY+V54xvtEyZuTJ0yurCgBvbgMZ4aIyHxnhojIfGeGiMh8Z4aIyHxnhojIfGeN5mfBwnLId6oR4sfXEAAAAASUVORK5CYII=";
        byte[] imageByte = org.apache.commons.codec.binary.Base64.decodeBase64((base64Data.substring(base64Data.indexOf(",") + 1)).getBytes());
        InputStream is = new ByteArrayInputStream(imageByte);
        String url = AwsS3.getInstance().upload("minutes-test-001.png", is,"image/png" , imageByte.length);
        assertNotNull(url);
        System.out.println("url: " + url);
    }

    @Test
    void delete() {
        AwsS3.getInstance().delete("minutes-test-001.png");
    }

    @Test
    void get() {
        String url = AwsS3.getInstance().getPresignedUrl("minutes-test-002.png");
        assertNotNull(url);
        System.out.println("url: " + url);
    }
}