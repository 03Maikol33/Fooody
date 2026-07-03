<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Il mio profilo – Fooody: visualizza e modifica i tuoi dati personali.">
    <title>Profilo – Fooody</title>
    <link rel="stylesheet" href="${context}/style.css">
    <link rel="stylesheet" href="${context}/fonts.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
</head>
<body>
    <div id="blurOverlay" class="blurOverlay"></div>

    <!-- HEADER -->
    <div class="headerBar">
        <div class="logo">
            <p class="logoText"><a href="${context}/index" style="text-decoration:none;color:inherit;">Fooody</a></p>
            <p class="logoDescriptionText">Ristorante e delivery</p>
        </div>
        <div class="navigationBar">
            <div><a href="${context}/index">Home</a></div>
            <div><a href="${context}/menu">Menu</a></div>
            <#if utente.ruolo == "cliente">
                <div><a href="${context}/orders">I miei ordini</a></div>
            <#elseif utente.ruolo == "proprietario">
                <div><a href="${context}/owner-orders" style="color:#ca0000;font-weight:bold;">Area Proprietario</a></div>
            <#elseif utente.ruolo == "personale" || utente.ruolo == "staff">
                <div><a href="${context}/staff-orders" style="color:#ca0000;font-weight:bold;">Area Staff</a></div>
            </#if>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
            <#if utente.ruolo == "cliente">
                <a href="${context}/cart" class="navCartLink nav-extra">
                    <span class="material-symbols-outlined">shopping_cart</span>
                </a>
            </#if>
            <a href="${context}/profile" class="accediBtn" style="text-decoration:none;">
                <span class="material-symbols-outlined">person</span>
                ${utente.nome}
            </a>
        </div>
        <span id="menuBtn" class="material-symbols-outlined menuIcon">menu</span>
    </div>

    <div id="mobileCircularMenu" class="mobileCircularMenu">
        <div class="mobileCircularMenuCloseContainer">
            <span id="closeMobileCircularMenuBtn" class="material-symbols-outlined">close</span>
        </div>
        <div><a href="${context}/index">Home</a></div>
        <div><a href="${context}/menu">Menu</a></div>
        <div><a href="${context}/index#contatti">Contatti</a></div>
        <#if utente.ruolo == "proprietario">
            <div><a href="${context}/owner-orders" style="color:#ffccd5;font-weight:bold;">Area Proprietario</a></div>
        <#elseif utente.ruolo == "personale" || utente.ruolo == "staff">
            <div><a href="${context}/staff-orders" style="color:#ffccd5;font-weight:bold;">Area Staff</a></div>
        <#else>
            <div><a href="${context}/orders">I miei ordini</a></div>
        </#if>
        <div><a href="${context}/profile">Profilo</a></div>
        <div><a href="${context}/logout">Esci</a></div>
    </div>

    <div class="pageContainer">

        <#-- Messaggi di feedback -->
        <#if successMsg??>
            <div class="successMsg" style="display:block;margin-bottom:16px;">${successMsg}</div>
        </#if>
        <#if errorMsg??>
            <div class="errorMsg" style="display:block;margin-bottom:16px;">${errorMsg}</div>
        </#if>

        <div class="profileHeader">
            <div class="profileAvatar" id="profileAvatar">${inizialeAvatar}</div>
            <div>
                <h1 class="pageTitle">
                    ${utente.nome} <span>${utente.cognome}</span>
                </h1>
                <p class="pageSubtitle">${utente.email}</p>
            </div>
        </div>

        <form method="post" action="${context}/profile" class="formCard">
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:20px;">Modifica Dati Personali</h2>
            <div class="formRow">
                <div class="formGroup">
                    <label for="nome">Nome *</label>
                    <input type="text" id="nome" name="nome" value="${utente.nome!''}" required>
                </div>
                <div class="formGroup">
                    <label for="cognome">Cognome *</label>
                    <input type="text" id="cognome" name="cognome" value="${utente.cognome!''}" required>
                </div>
            </div>
            <div class="formRow">
                <div class="formGroup">
                    <label>Email <small>(non modificabile)</small></label>
                    <input type="email" value="${utente.email!''}" readonly style="background:#f5f5f5;color:#888;">
                </div>
                <div class="formGroup">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono" value="" placeholder="+39 ...">
                </div>
            </div>
            <div class="formRow">
                <div class="formGroup" style="flex: 2;">
                    <label for="via">Via / Piazza</label>
                    <input type="text" id="via" name="via" value="" placeholder="Via Roma">
                </div>
                <div class="formGroup" style="flex: 1;">
                    <label for="civico">Civico</label>
                    <input type="text" id="civico" name="civico" value="" placeholder="1">
                </div>
            </div>
            <div class="formGroup">
                <label for="citta">Città</label>
                <input type="text" id="citta" name="citta" value="" placeholder="Alba Adriatica">
            </div>
            <div class="formGroup">
                <label>Ruolo</label>
                <input type="text" value="${utente.ruolo!''}" readonly style="background:#f5f5f5;color:#888;text-transform:capitalize;">
            </div>

            <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">
                <span class="material-symbols-outlined">save</span> Salva Modifiche
            </button>
        </form>

        <div class="formCard">
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:20px;">Account</h2>
            <div style="display:flex;gap:12px;flex-wrap:wrap;">
                <a href="${context}/orders" class="btnOutline">
                    <span class="material-symbols-outlined">receipt_long</span> I miei ordini
                </a>
                <a href="${context}/logout" class="btnGray" style="text-decoration:none;">
                    <span class="material-symbols-outlined">logout</span> Esci
                </a>
            </div>
        </div>
    </div>

    <footer>
        <div>
            <p>Contatti:</p>
            <p>Telefono: +39 123 456 789</p>
            <p>Email: info@fooody.it</p>
        </div>
        <div>
            <p>Indirizzo:</p>
            <p>Via Roma 1, Alba Adriatica</p>
        </div>
        <p>&copy; 2026 Fooody.</p>
    </footer>

    <script src="${context}/scripts/app.js"></script>
</body>
</html>
