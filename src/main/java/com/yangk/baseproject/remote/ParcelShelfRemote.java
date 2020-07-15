package com.yangk.baseproject.remote;

import com.yangk.baseproject.domain.dto.ParcelShelfDTO;
import com.yangk.baseproject.domain.response.ResponseMsg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description 测试远程调用
 * @Author yangkun
 * @Date 2020/6/29
 * @Version 1.0
 * @blame yangkun
 */
@FeignClient(value = "gds-wos-ussfoa")
public interface ParcelShelfRemote {

    @PostMapping("/app/parcel/shelf")
    ResponseMsg parcelShelf(@RequestBody ParcelShelfDTO parcelShelfDTO);
}
