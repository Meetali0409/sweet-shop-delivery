package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.AddressDto
import com.sweetshop.backend.entity.Address

fun Address.toDto(): AddressDto = AddressDto(
    id = this.id,
    userId = this.user.id,
    name = this.name,
    phone = this.phone,
    addressLine1 = this.addressLine1,
    addressLine2 = this.addressLine2,
    city = this.city,
    state = this.state,
    pincode = this.pincode,
    landmark = this.landmark,
    isDefault = this.isDefault,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
