package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.OrderDto
import com.sweetshop.backend.dto.OrderItemDto
import com.sweetshop.backend.dto.OrderListDto
import com.sweetshop.backend.entity.Address
import com.sweetshop.backend.entity.Order
import com.sweetshop.backend.entity.OrderItem

fun Order.toDto(address: Address?): OrderDto = OrderDto(
    id = this.id,
    orderNumber = this.orderNumber,
    userId = this.user.id,
    customerName = this.user.name,
    customerPhone = this.user.phone,
    address = address?.toDto(),
    items = this.items.map { it.toDto() },
    subtotal = this.subtotal,
    discount = this.discount,
    deliveryCharge = this.deliveryCharge,
    tax = this.tax,
    totalAmount = this.totalAmount,
    paymentMethod = this.paymentMethod,
    paymentStatus = this.paymentStatus,
    orderStatus = this.orderStatus,
    couponCode = this.couponCode,
    notes = this.notes,
    estimatedDelivery = this.estimatedDelivery,
    createdAt = this.createdAt
)

fun Order.toListDto(): OrderListDto = OrderListDto(
    id = this.id,
    orderNumber = this.orderNumber,
    items = this.items.map { it.toDto() },
    totalAmount = this.totalAmount,
    orderStatus = this.orderStatus,
    paymentStatus = this.paymentStatus,
    itemCount = this.items.size,
    createdAt = this.createdAt
)

fun OrderItem.toDto(): OrderItemDto = OrderItemDto(
    id = this.id,
    productId = this.product.id,
    productName = this.productName,
    productImage = this.productImage,
    quantity = this.quantity,
    selectedWeight = this.selectedWeight,
    unitPrice = this.unitPrice,
    totalPrice = this.totalPrice
)
