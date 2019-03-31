package com.ecommerce.microcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OTHMANI Lazhar
 * on 31/03/2019
 */

@Builder @Data
@AllArgsConstructor @NoArgsConstructor
public class PrixMargeResponseDTO {
    private Product product;
    private int marge;
}
