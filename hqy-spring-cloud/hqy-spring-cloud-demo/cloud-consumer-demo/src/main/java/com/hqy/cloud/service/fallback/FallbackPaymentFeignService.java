package com.hqy.cloud.service.fallback;

import com.hqy.cloud.service.PaymentFeignService;
import com.hqy.fundation.common.bind.DataResponse;
import org.springframework.stereotype.Component;

/**
 * @author qy
 * @create 2021/7/25 14:47
 */
@Component
public class FallbackPaymentFeignService implements PaymentFeignService {

    @Override
    public DataResponse getPaymentById(Long id) {
        return new DataResponse(false, "return fallback -> try again latter", 500, null);
    }
}
