package com.exequt.ecom.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CartRepositoryFacade {

    private final CartRepository cartRepository;


}
