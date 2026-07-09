/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */
// File này hoàn toàn là Javascript thuần, không chứa code Java/JSP nữa

// 1. Khởi tạo trạng thái phiên bản đang chọn mặc định
let selectedCapacity = variants.length > 0 ? variants[0].capacity : '';
let selectedColor = variants.length > 0 ? variants[0].color : '';

const uniqueCapacities = [...new Set(variants.map(v => v.capacity))];
const uniqueColors = [...new Set(variants.map(v => v.color))];

function renderVariantButtons() {
    if (variants.length === 0) return;

    const capContainer = document.getElementById('capacity-container');
    if (capContainer) {
        capContainer.innerHTML = uniqueCapacities.map(cap => `
            <button type="button" class="btn btn-outline-danger ${cap === selectedCapacity ? 'active' : ''}" 
                    onclick="changeCapacity('${cap}')">
                ${cap}
            </button>
        `).join('');
    }

    const colorContainer = document.getElementById('color-container');
    if (colorContainer) {
        colorContainer.innerHTML = uniqueColors.map(col => {
            const isAvailable = variants.some(v => v.capacity === selectedCapacity && v.color === col);
            return `
                <button type="button" class="btn btn-outline-danger ${col === selectedColor ? 'active' : ''}" 
                        ${!isAvailable ? 'disabled style="opacity: 0.4; cursor: not-allowed;"' : ''}
                        onclick="changeColor('${col}')">
                    ${col}
                </button>
            `;
        }).join('');
    }

    updateSelectedVariantInfo();
}

function changeCapacity(cap) {
    selectedCapacity = cap;
    const checkMatch = variants.find(v => v.capacity === selectedCapacity && v.color === selectedColor);
    if (!checkMatch) {
        const fallback = variants.find(v => v.capacity === selectedCapacity);
        if (fallback) selectedColor = fallback.color;
    }
    renderVariantButtons();
}

function changeColor(col) {
    selectedColor = col;
    renderVariantButtons();
}

function updateSelectedVariantInfo() {
    const matchedVariant = variants.find(v => v.capacity === selectedCapacity && v.color === selectedColor);
    
    if (matchedVariant) {
        document.getElementById('selected-variant-id').value = matchedVariant.id;
        document.getElementById('display-price').innerText = new Intl.NumberFormat('vi-VN').format(matchedVariant.price) + ' đ';

        let qtyInput = document.getElementById('order-quantity');
        let btnCart = document.getElementById('btn-add-cart');

        if (qtyInput && btnCart) {
            if (matchedVariant.stock > 0) {
                qtyInput.max = matchedVariant.stock;
                if (parseInt(qtyInput.value) > matchedVariant.stock) qtyInput.value = matchedVariant.stock;
                if (parseInt(qtyInput.value) <= 0) qtyInput.value = 1;
                btnCart.disabled = false;
                btnCart.innerText = "THÊM VÀO GIỎ HÀNG";
            } else {
                qtyInput.max = 0;
                qtyInput.value = 0;
                btnCart.disabled = true;
                btnCart.innerText = "HẾT HÀNG";
            }
        }
    }
}

document.addEventListener("DOMContentLoaded", renderVariantButtons);