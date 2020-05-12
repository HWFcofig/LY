package com.leyou.auth.text;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    //公钥
    private static final String pubKeyPath = "D:\\ithmxm\\tmp\\rsa\\rsa.pub";

    //私钥
    private static final String priKeyPath = "D:\\ithmxm\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU4ODc1NzU1N30.gMk0A8ifV7fAWC7MGivqru_Asii2smqf3b8dcYZcnHiCkuYCKhaA2AckAXQ53hGLQs_hET-OD5UUlayyctDsdLezaU3yMJhunAvMjygPAfoijncclYH84uVsbgSlqvD9KI5zhjvbb4_D8-GZxRX9E6m6GgbopM8R1Ln8iTSDjW8";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}