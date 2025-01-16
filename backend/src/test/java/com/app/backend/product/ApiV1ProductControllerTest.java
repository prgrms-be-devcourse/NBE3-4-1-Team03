package com.app.backend.product;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private ProductRepository productRepository;

    @BeforeAll
    public static void setProducts(ApplicationContext applicationContext){
        ProductRepository productRepository = applicationContext.getBean(ProductRepository.class);
        if(productRepository.count()>0) return;
        for(int i=0;i<10;i++){
            productRepository.save(
                    Product.builder()
                            .name("상품 %d".formatted(i+1))
                            .price(BigDecimal.valueOf(10000 - i*500))
                            .description("상세설명")
                            .stock(100)
                            .image(null)
                            .status(true)
                            .build());
        }
    }

    @Test
    @DisplayName("상품 단일 조회")
    void itemTest1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products/1")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("1번 상품 상세사항입니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("상품 1"))
                .andExpect(jsonPath("$.data.description").value("상세설명"))
                .andExpect(jsonPath("$.data.price").value(10000.00))
                .andExpect(jsonPath("$.data.amount").value(100))
                .andExpect(jsonPath("$.data.status").value(true))
        ;

    }

    @Test
    @DisplayName("상품 전체 조회 (default)")
    void itemsTest1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(10))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 10"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(5500))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;

    }

    @Test
    @DisplayName("상품 전체 조회 (페이징 파라미터 전달)")
    void itemsTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?page=2&size=3")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(4))
                .andExpect(jsonPath("$.data.currentPage").value(2))
                .andExpect(jsonPath("$.data.pageSize").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(true))
                .andExpect(jsonPath("$.data.isLast").value(false))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(7))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 7"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(7000))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 (sort 파라미터 전달 : price")
    void itemsTest3() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?sort=price")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(1))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 1"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(10000))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 (sort 파라미터 전달 : name")
    void itemsTest4() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?sort=name")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(9))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 9"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(6000))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 (direction 오름차순 정렬)")
    void itemsTest5() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?direction=asc")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(1))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 1"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(10000))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 (keyword 검색)")
    void itemsTest6() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?keyword=1")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("상품 페이지 전체조회"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.product_info").exists())
                .andExpect(jsonPath("$.data.product_info[0].product_id").value(10))
                .andExpect(jsonPath("$.data.product_info[0].product_name").value("상품 10"))
                .andExpect(jsonPath("$.data.product_info[0].product_price").value(5500.00))
                .andExpect(jsonPath("$.data.product_info[0].product_amount").value(100))
                .andExpect(jsonPath("$.data.product_info[0].product_status").value(true))
        ;
    }


    @Test
    @DisplayName("상품 전체 조회 예외 (page 파라미터값 이상)")
    void itemsExceptionTest1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?page=xx")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 예외 (sort 파라미터 값 이상)")
    void itemsExceptionTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?sort=dinamic")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("P004"))
                .andExpect(jsonPath("$.message").value("요청한 정렬 조건이 존재하지 않음"))
        ;
    }

    @Test
    @DisplayName("상품 전체 조회 예외 (direction 파라미터 값 이상)")
    void itemsExceptionTest3() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products?direction=under")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("P005"))
                .andExpect(jsonPath("$.message").value("요청한 정렬 방향이 존재하지 않음"))
        ;
    }

    @Test
    @DisplayName("상품 상세 조회 예외 (존재하지 않는 상품 Id)")
    void itemExceptionTest1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products/121412414")
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("P002"))
                .andExpect(jsonPath("$.message").value("제품 정보가 존재하지 않음"))
        ;
    }

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


        Product product = productService.findById(11L);

        assertThat(product.getName()).isEqualTo("testProduct");
        assertThat(product.getDescription()).isEqualTo("testDescription");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(product.getStock()).isEqualTo(1500);
        assertThat(product.getStatus()).isTrue();
    }
}
