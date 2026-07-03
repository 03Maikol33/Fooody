// =============================================
// WEBDELIVERY – LOGICA APP (sessione, carrello)
//*/ =============================================
/*
var WD = window.WD || {};

// --- AUTENTICAZIONE ---
WD.Auth = {
    getSession: function() {
        try {
            var s = sessionStorage.getItem('wd_session');
            return s ? JSON.parse(s) : null;
        } catch(e) { return null; }
    },
    setSession: function(user) {
        var session = {
            id: user.id,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            role: user.role
        };
        sessionStorage.setItem('wd_session', JSON.stringify(session));
        return session;
    },
    logout: function() {
        sessionStorage.removeItem('wd_session');
        WD.Cart.clear();
        window.location.href = '../index.html';
    },
    login: function(email, password) {
        var users = WD.USERS || [];
        for (var i = 0; i < users.length; i++) {
            if (users[i].email === email && users[i].password === password) {
                var session = WD.Auth.setSession(users[i]);
                return { ok: true, user: session };
            }
        }
        return { ok: false, error: 'Email o password non corretti.' };
    },
    register: function(data) {
        var users = WD.USERS || [];
        for (var i = 0; i < users.length; i++) {
            if (users[i].email === data.email) {
                return { ok: false, error: 'Email già registrata.' };
            }
        }
        var newUser = {
            id: 'u' + (users.length + 1),
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
            password: data.password,
            phone: data.phone,
            address: data.address,
            role: 'customer'
        };
        WD.USERS.push(newUser);
        var session = WD.Auth.setSession(newUser);
        return { ok: true, user: session };
    },
    findUser: function(email) {
        var users = WD.USERS || [];
        for (var i = 0; i < users.length; i++) {
            if (users[i].email === email) return users[i];
        }
        return null;
    },
    requireRole: function(roles) {
        var session = WD.Auth.getSession();
        if (!session) {
            window.location.href = 'login.html';
            return false;
        }
        if (typeof roles === 'string') roles = [roles];
        if (roles.indexOf(session.role) === -1) {
            window.location.href = '../index.html';
            return false;
        }
        return true;
    }
};

// --- CARRELLO ---
WD.Cart = {
    _key: 'wd_cart',
    get: function() {
        try {
            var c = sessionStorage.getItem(WD.Cart._key);
            return c ? JSON.parse(c) : [];
        } catch(e) { return []; }
    },
    save: function(cart) {
        sessionStorage.setItem(WD.Cart._key, JSON.stringify(cart));
    },
    add: function(productId, quantity, selectedFeatures) {
        var product = WD.Products.getById(productId);
        if (!product) return;
        var price = WD.Products.calcFinalPrice(product, selectedFeatures);
        var cart = WD.Cart.get();
        var item = {
            productId: productId,
            productName: product.name,
            quantity: quantity,
            selectedFeatures: selectedFeatures,
            unitPriceFinal: price,
            prepTime: product.prepTime
        };
        cart.push(item);
        WD.Cart.save(cart);
    },
    remove: function(idx) {
        var cart = WD.Cart.get();
        cart.splice(idx, 1);
        WD.Cart.save(cart);
    },
    updateQty: function(idx, qty) {
        var cart = WD.Cart.get();
        if (!cart[idx]) return;
        if (qty <= 0) {
            cart.splice(idx, 1);
        } else {
            cart[idx].quantity = qty;
        }
        WD.Cart.save(cart);
    },
    clear: function() {
        sessionStorage.removeItem(WD.Cart._key);
    },
    count: function() {
        var cart = WD.Cart.get();
        return cart.reduce(function(s, it) { return s + it.quantity; }, 0);
    },
    getTotal: function() {
        var cart = WD.Cart.get();
        return cart.reduce(function(s, it) { return s + it.unitPriceFinal * it.quantity; }, 0);
    },
    getTotalPrepTime: function() {
        var cart = WD.Cart.get();
        return cart.reduce(function(s, it) { return s + (it.prepTime || 0) * it.quantity; }, 0);
    }
};

// --- NAVBAR DINAMICA ---
WD.NavBar = {
    update: function() {
        var session = WD.Auth.getSession();
        var accediBtn = document.getElementById('accediBtn') || document.querySelector('.accediBtn');
        if (!accediBtn) return;

        if (!session) {
            accediBtn.innerHTML = '<span class="material-symbols-outlined">login</span> Accedi';
            accediBtn.onclick = function() { window.location.href = 'pages/login.html'; };
            document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
            document.querySelectorAll('.nav-customer').forEach(function(el) { el.style.display = 'none'; });
        } else {
            var icon = session.role === 'owner' ? 'manage_accounts' : (session.role === 'staff' ? 'badge' : 'account_circle');
            accediBtn.innerHTML = '<span class="material-symbols-outlined">' + icon + '</span> ' + session.firstName;
            if (session.role === 'owner') {
                accediBtn.onclick = function() { window.location.href = 'pages/owner-orders.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
            } else if (session.role === 'staff') {
                accediBtn.onclick = function() { window.location.href = 'pages/staff-orders.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
            } else {
                // customer
                accediBtn.onclick = function() { window.location.href = 'pages/profile.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = ''; });
                var cartLink = document.getElementById('navCartLink');
                if (cartLink) cartLink.style.display = 'flex';
            }
        }
        WD.NavBar.updateCartBadge();
    },
    updateInner: function() {
        // Per pagine dentro /pages/
        var session = WD.Auth.getSession();
        var accediBtn = document.getElementById('accediBtn') || document.querySelector('.accediBtn');
        if (!accediBtn) return;

        if (!session) {
            accediBtn.innerHTML = '<span class="material-symbols-outlined">login</span> Accedi';
            accediBtn.onclick = function() { window.location.href = 'login.html'; };
            document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
        } else {
            var icon = session.role === 'owner' ? 'manage_accounts' : (session.role === 'staff' ? 'badge' : 'account_circle');
            accediBtn.innerHTML = '<span class="material-symbols-outlined">' + icon + '</span> ' + session.firstName;
            if (session.role === 'owner') {
                accediBtn.onclick = function() { window.location.href = 'owner-orders.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
            } else if (session.role === 'staff') {
                accediBtn.onclick = function() { window.location.href = 'staff-orders.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = 'none'; });
            } else {
                // customer
                accediBtn.onclick = function() { window.location.href = 'profile.html'; };
                document.querySelectorAll('.nav-extra').forEach(function(el) { el.style.display = ''; });
                var cartLink = document.getElementById('navCartLink');
                if (cartLink) cartLink.style.display = 'flex';
            }
        }
        // Aggiorna badge carrello
        WD.NavBar.updateCartBadge();
    },
    updateCartBadge: function() {
        var badge = document.getElementById('cartBadgeCount');
        if (badge) {
            var cnt = WD.Cart.count();
            badge.textContent = cnt;
            badge.style.display = cnt > 0 ? 'flex' : 'none';
        }
    }
};

// --- TOAST ---
WD.toast = function(msg, type) {
    var t = document.createElement('div');
    t.className = 'wd-toast wd-toast-' + (type || 'info');
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(function() { t.classList.add('show'); }, 10);
    setTimeout(function() {
        t.classList.remove('show');
        setTimeout(function() { document.body.removeChild(t); }, 400);
    }, 3000);
};*/

// menu laterale mobile
document.addEventListener('DOMContentLoaded', function() {
    var menuBtn = document.getElementById('menuBtn');
    var closeBtn = document.getElementById('closeMobileCircularMenuBtn');
    if (menuBtn) {
        menuBtn.addEventListener('click', function() {
            var menu = document.getElementById('mobileCircularMenu');
            var overlay = document.getElementById('blurOverlay');
            if (menu) { menu.style.display = 'block'; setTimeout(function() { menu.style.transform = 'translateX(0)'; }, 10); }
            if (overlay) overlay.style.display = 'block';
        });
    }
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            var menu = document.getElementById('mobileCircularMenu');
            var overlay = document.getElementById('blurOverlay');
            if (menu) { menu.style.transform = 'translateX(100%)'; setTimeout(function() { menu.style.display = 'none'; }, 500); }
            if (overlay) overlay.style.display = 'none';
        });
    }
});
