<!DOCTYPE html>
<html lang="it">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="${context}/style.css">
    <link rel="stylesheet" href="${context}/fonts.css">
    <meta name="description" content="Fooody – Ristorante e delivery ad Alba Adriatica. Ordina a domicilio online!">
    <link rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0" />
    <title>Fooody – Ristorante e Delivery</title>
</head>

<body>
    <div id="blurOverlay" class="blurOverlay"></div>
    <div class="headerBar">
        <div class="logo">
            <p class="logoText">Fooody</p>
            <p class="logoDescriptionText">Ristorante e delivery</p>
        </div>
        <div class="navigationBar">
            <div><a href="${context}/index" style="color:#ca0000;font-weight:bold;">Home</a></div>
            <div><a href="${context}/menu">Menu</a></div>
            <div><a href="#contatti">Contatti</a></div>
            <#if utenteLoggato?? && utenteLoggato.ruolo == "cliente">
                <div class="nav-extra"><a href="${context}/orders">I miei ordini</a></div>
            <#elseif utenteLoggato?? && utenteLoggato.ruolo == "proprietario">
                <div class="nav-extra"><a href="${context}/owner-orders" style="color:#ca0000;font-weight:bold;">Area Proprietario</a></div>
            <#elseif utenteLoggato?? && (utenteLoggato.ruolo == "personale" || utenteLoggato.ruolo == "staff")>
                <div class="nav-extra"><a href="${context}/staff-orders" style="color:#ca0000;font-weight:bold;">Area Staff</a></div>
            </#if>
        </div>
        <div style="display:flex;align-items:center;gap:8px;">

            <#-- SE L'UTENTE E' LOGGATO -->
            <#if utenteLoggato??>

                <#-- Se è un cliente, mostriamo il carrello -->
                <#if utenteLoggato.ruolo == "cliente">
                    <a href="${context}/cart" id="navCartLink" class="navCartLink nav-extra">
                        <span class="material-symbols-outlined">shopping_cart</span>
                    </a>
                </#if>

                <#-- Pulsante Profilo -->
                <a href="${context}/profile" class="accediBtn" style="text-decoration:none;">
                    <span class="material-symbols-outlined">person</span>
                    ${utenteLoggato.nome}
                </a>

            <#-- SE L'UTENTE NON E' LOGGATO (Ospite) -->
            <#else>
                <a href="${context}/login" class="accediBtn" style="text-decoration:none;">
                    <span class="material-symbols-outlined">login</span>
                    Accedi
                </a>
            </#if>

        </div>

        <span id="menuBtn" class="material-symbols-outlined menuIcon">
            menu
        </span>
    </div>
    <div id="mobileCircularMenu" class="mobileCircularMenu">
        <div class="mobileCircularMenuCloseContainer">
            <span id="closeMobileCircularMenuBtn" class="material-symbols-outlined">
                close
            </span>
        </div>
        <div><a href="${context}/index">Home</a></div>
        <div><a href="${context}/menu">Menu</a></div>
        <div><a href="#contatti">Contatti</a></div>
        <#if utenteLoggato??>
            <#if utenteLoggato.ruolo == "proprietario">
                <div><a href="${context}/owner-orders" style="color:#ffccd5;font-weight:bold;">Area Proprietario</a></div>
            <#elseif utenteLoggato.ruolo == "personale" || utenteLoggato.ruolo == "staff">
                <div><a href="${context}/staff-orders" style="color:#ffccd5;font-weight:bold;">Area Staff</a></div>
            <#else>
                <div><a href="${context}/orders">I miei ordini</a></div>
            </#if>
            <div><a href="${context}/profile">Profilo</a></div>
            <div><a href="${context}/logout">Esci</a></div>
        <#else>
            <div><a href="${context}/login">Accedi</a></div>
        </#if>
    </div>

    <div class="bannerPresentazione">
        <div>
            <h2>
                Ordini<br> a domicilio<br> in ogni<br> momento!
            </h2>
            <p>Ordina a domicilio<br> in pochi minuti!</p>
        </div>
        <div class="bannerImage">
            <a href="${context}/menu" class="vaiMenuBtn" style="text-decoration:none;">
                Vai al menu
                <span class="material-symbols-outlined">fastfood</span>
            </a>
        </div>
    </div>
    <hr>
    <div class="menuPreviewContainer">
        <h2 class="doubleColorTitle">
            <div>Un'anteprima del nostro</div>
            <div>Menù</div>
            <p class="subtitle">Dai un'occhiata alle nostre specialità!</p>
        </h2>
    </div>

    <div class="menuPreviewItemsContainer">
        <#list prodotti as prodotto>
            <div class="menuPreviewItem">
                <img class="menuItemImage" src="${context}/img/2.jpg" alt="${prodotto.nome}">
                <div style="display: flex; flex-direction: row; justify-content: space-between; align-items: center;">
                    <p class="menuItemCategoria">${prodotto.categoria}</p>
                    <p style="margin-left: 7px;" class="prezzo">${prodotto.prezzoBase}€</p>
                </div>
                <h3 class="menuItemTitle">${prodotto.nome}</h3>
                <p class="menuItemTextLabel">Descrizione:</p>
                <p class="menuItemDescription">${prodotto.descrizione}</p>
            </div>
        </#list>

        <#if prodotti?size == 0>
            <p>I prodotti sono in fase di caricamento</p>
        </#if>
    </div>

    <hr>
    <div class="contattiContainer"
        style="display: flex; flex-direction: column; align-items: center; gap: 18px; padding: 32px 20px; text-align: center;">
        <h2 class="doubleColorTitle" style="margin: 0;">
            <div>I nostri</div>
            <div>Contatti</div>
        </h2>
        <p class="subtitle" style="margin: 0; max-width: 560px;">
            Hai bisogno di informazioni, prenotazioni o vuoi ordinare? Scrivici o chiamaci, siamo a tua disposizione.
        </p>
        <div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 14px; width: 100%;">
            <div
                style="display: flex; flex-direction: column; gap: 6px; min-width: 220px; padding: 16px 20px; border-radius: 16px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12);">
                <span style="font-size: 0.9rem; opacity: 0.8;">Telefono</span>
                <a href="tel:+39123456789" style="text-decoration: none; font-weight: 600;">+39 123 456 789</a>
            </div>
            <div
                style="display: flex; flex-direction: column; gap: 6px; min-width: 220px; padding: 16px 20px; border-radius: 16px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12);">
                <span style="font-size: 0.9rem; opacity: 0.8;">Email</span>
                <a href="mailto:info@fooody.it" style="text-decoration: none; font-weight: 600;">info@fooody.it</a>
            </div>
        </div>
    </div>

    <hr>
    <div class="doveTrovarciContainer">
        <h2 class="doubleColorTitle">
            <div>Dove puoi</div>
            <div>Trovarci</div>
        </h2>
        <p class="subtitle">Vieni a trovarci nel nostro ristorante!</p>
        <p class="subtitle">Ci trovi ad Alba Adriatica in Via Roma 1</p>
        <iframe
            src="https://www.google.com/maps/embed?pb=!1m14!1m12!1m3!1d2688.1610142356258!2d13.919973400134053!3d42.83201358173305!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!5e1!3m2!1sit!2sit!4v1782313555020!5m2!1sit!2sit"
            width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy"
            referrerpolicy="strict-origin-when-cross-origin"></iframe>
    </div>

    <footer id="contatti">
        <div>
            <p>Contatti:</p>
            <p>Telefono: +39 123 456 789</p>
            <p>Email: info@fooody.it</p>
        </div>
        <div>
            <p>Indirizzo:</p>
            <p>Via Roma 1, Alba Adriatica</p>
            <a href="${context}/menu">Menu completo</a>
            <a href="${context}/login">Accedi / Registrati</a>
        </div>
        <p>&copy; 2026 Fooody. Tutti i diritti riservati.</p>
    </footer>
    <script src="${context}/scripts/app.js"></script>

</body>

</html>