package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class FeignClientBaseClass {

    protected abstract String getServiceName();

    protected <T> T makeARequest(Supplier<ResponseEntity<T>> request) {
        return makeARequest(request, () -> null);
    }

    protected <T> T makeARequest(Supplier<ResponseEntity<T>> request, Supplier<T> orElseGet) {
        var response = requestFeignClient(request);
        if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return orElseGet.get();
        }
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return orElseGet.get();
        }
        var responseBody = response.getBody();
        if (responseBody == null) {
            return orElseGet.get();
        }
        //noinspection unchecked
        return (T) responseBody;
    }

    protected <T> ResponseEntity<?> requestFeignClient(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (feign.FeignException e) {
            // If 404
            if (Objects.equals(e.status(), HttpStatus.NOT_FOUND.value())) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(e.responseBody(), HttpStatusCode.valueOf(e.status()));
        }
    }

    protected void logRemoteServiceError(ResponseEntity<?> response) {
        log.error("Error during performing request to remote service '{}'. {} - {}",
                getServiceName(),
                response == null ? "<no response status>" : response.getStatusCode(),
                response == null ? "<no response body>" : response.getBody());
    }
}
