package com.exequt.ecom.mapper;

import com.exequt.ecom.model.ProductEntity;
import com.exequt.ecom.model.Product;
import com.exequt.ecom.model.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    List<Product> mapToProductResponses(List<ProductEntity> productEntities);

    @Mapping(target = "products", source = "products")
    @Mapping(target = "nextCursor", source = "nextCurtsor")
    @Mapping(target = "hasNext", source = "hasNext")
    ProductResponse mapToProductResponse(List<Product> products, Integer nextCurtsor, boolean hasNext);
}
