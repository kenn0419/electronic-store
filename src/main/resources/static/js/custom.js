(function () {
	'use strict';

	var tinyslider = function () {
		var el = document.querySelectorAll('.testimonial-slider');

		if (el.length > 0) {
			var slider = tns({
				container: '.testimonial-slider',
				items: 1,
				axis: "horizontal",
				controlsContainer: "#testimonial-nav",
				swipeAngle: false,
				speed: 700,
				nav: true,
				controls: true,
				autoplay: true,
				autoplayHoverPause: true,
				autoplayTimeout: 3500,
				autoplayButtonOutput: false
			});
		}
	};
	tinyslider();




	var sitePlusMinus = function () {

		var value,
			quantity = document.getElementsByClassName('quantity-container');
		var totalPriceAll = document.querySelectorAll('.total-price');
		var totalCart = document.querySelector('#total-cart');
		function createBindings(quantityContainer) {
			var quantityAmount = quantityContainer.getElementsByClassName('quantity-amount')[0];
			var index = quantityAmount.getAttribute("data-cart-detail-index") * 1;
			var increase = quantityContainer.getElementsByClassName('increase')[0];
			var decrease = quantityContainer.getElementsByClassName('decrease')[0];
			var price = quantityAmount.getAttribute("data-cart-detail-price");
			var totalPrice = document.querySelector(`.total-price[data-cart-detail-index="${index}"]`);
			increase.addEventListener('click', function (e) { increaseValue(e, quantityAmount, price, totalPrice); });
			decrease.addEventListener('click', function (e) { decreaseValue(e, quantityAmount, price, totalPrice); });
		}

		function init() {
			for (var i = 0; i < quantity.length; i++) {
				createBindings(quantity[i]);
			}
		};

		function increaseValue(event, quantityAmount, price, totalPrice) {
			value = parseInt(quantityAmount.value, 10);

			value = isNaN(value) ? 0 : value;
			value++;
			quantityAmount.value = value;

			updateTotalPrice(value, price, totalPrice);

			updateFormQuantity(quantityAmount);

			updateTotalPriceAll();

		}

		function decreaseValue(event, quantityAmount, price, totalPrice) {
			value = parseInt(quantityAmount.value, 10);

			value = isNaN(value) ? 0 : value;
			if (value > 0) value--;

			quantityAmount.value = value;

			updateTotalPrice(value, price, totalPrice);

			updateFormQuantity(quantityAmount);

			updateTotalPriceAll();
		}

		function updateTotalPriceAll() {
			let totalCartPrice = 0;
			document.querySelectorAll('.quantity-amount').forEach(function (quantityElement) {
				let quantity = parseInt(quantityElement.value, 10);
				let price = parseFloat(quantityElement.getAttribute("data-cart-detail-price"));

				totalCartPrice += quantity * price;
			});

			// Cập nhật tổng giá giỏ hàng
			document.getElementById('total-cart').textContent = formatCurrency(totalCartPrice) + 'đ';
		}

		function updateTotalPrice(quantity, price, totalPriceElement) {
			let total = quantity * price;
			totalPriceElement.textContent = formatCurrency(total) + 'đ';
		}

		function updateFormQuantity(quantityAmount) {
			var index = quantityAmount.getAttribute("data-cart-detail-index");
			// Tìm input số lượng trong form bằng index
			var formQuantityInput = document.querySelector(`input[name="cartDetails[${index}].quantity"]`);
			if (formQuantityInput) {
				formQuantityInput.value = quantityAmount.value;
			}
		}

		function formatCurrency(value) {
			const formatter = new Intl.NumberFormat('vi-VN', {
				style: 'decimal',
				minimumFractionDigits: 0,
			});

			let formatted = formatter.format(value);

			formatted = formatted.replace(/\./g, ',');
			return formatted;
		}

		init();

	};
	sitePlusMinus();


})()