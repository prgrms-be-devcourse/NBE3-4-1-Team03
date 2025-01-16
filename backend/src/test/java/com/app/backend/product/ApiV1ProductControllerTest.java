package com.app.backend.product;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품 등록")
    void addProductTest() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/products")
                                .content("""
                                        {
                                            "name": "testProduct",
                                            "description": "testDescription",
                                            "price": 25000,
                                            "amount": 1500,
                                            "status": true
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("상품 'testProduct'이(가) 등록되었습니다."))
                .andExpect(jsonPath("$.code").value("201"));


        Product product = productService.findById(1L);

        assertThat(product.getName()).isEqualTo("testProduct");
        assertThat(product.getDescription()).isEqualTo("testDescription");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(product.getStock()).isEqualTo(1500);
        assertThat(product.getStatus()).isTrue();
    }
}
