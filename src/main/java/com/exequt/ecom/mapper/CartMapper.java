package com.exequt.ecom.mapper;

import com.exequt.ecom.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "cartId", source = "id")
    CartResponse toCartResponse(CartEntity cartEntity);

    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalItems", expression = "java(mapTotalItems(cart.getItems()))")
    @Mapping(target = "subtotal", expression = "java(mapSubtotal(cart.getItems()))")
    @Mapping(target = "totalPrice", expression = "java(mapSubtotal(cart.getItems()))")
    @Mapping(target = "currency", expression = "java(resolveCurrency(cart))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CartDetailsResponse toCartDetailsResponse(CartEntity cart);

    List<CartItemDetailsResponse> toItemResponseList(List<CartItemEntity> items);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "category", source = "product.category")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "totalPrice", expression = "java(mapItemTotalPrice(item))")
    @Mapping(target = "inStock", expression = "java(isInStock(item))")
    @Mapping(target = "availableStock", source = "product.numberInStock")
    CartItemDetailsResponse toItemResponse(CartItemEntity item);

    default BigDecimal mapItemTotalPrice(CartItemEntity item) {
        return item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    default Integer mapTotalItems(List<CartItemEntity> items) {
        return items == null ? 0 :
                items.stream()
                        .mapToInt(CartItemEntity::getQuantity)
                        .sum();
    }

    default BigDecimal mapSubtotal(List<CartItemEntity> items) {
        if (items == null) return BigDecimal.ZERO;

        return items.stream()
                .map(i -> i.getUnitPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Boolean isInStock(CartItemEntity item) {
        return item.getProduct().getNumberInStock() >= item.getQuantity();
    }

    default String resolveCurrency(CartEntity cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return "N/A";
        }
        return cart.getItems().get(0).getProduct().getCurrency();
    }
}
