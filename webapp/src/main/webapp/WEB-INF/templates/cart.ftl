<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Il tuo carrello – Fooody: rivedi e conferma il tuo ordine.">
    <title>Carrello – Fooody</title>
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
            <div><a href="${context}/orders">I miei ordini</a></div>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
            <a href="${context}/cart" class="navCartLink nav-extra" style="color:#ca0000;">
                <span class="material-symbols-outlined">shopping_cart</span>
                <#if carrello?has_content>
                    <span style="background:#ca0000;color:white;border-radius:50%;padding:2px 6px;font-size:11px;font-weight:bold;">
                        ${carrello?size}
                    </span>
                </#if>
            </a>
            <#if utenteLoggato??>
                <a href="${context}/profile" class="accediBtn" style="text-decoration:none;">
                    <span class="material-symbols-outlined">person</span>
                    ${utenteLoggato.nome}
                </a>
            </#if>
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

    <div class="pageContainer">
        <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;margin-bottom:24px;">
            <div>
                <h1 class="pageTitle">Il tuo <span>Carrello</span></h1>
                <p class="pageSubtitle">Rivedi i prodotti selezionati e scegli l'orario di consegna.</p>
            </div>
            <a href="${context}/menu" class="btnOutline">
                <span class="material-symbols-outlined">arrow_back</span> Continua a ordinare
            </a>
        </div>

        <#-- Messaggi di feedback -->
        <#if successMsg??>
            <div class="successMsg" style="display:block;margin-bottom:16px;">${successMsg}</div>
        </#if>
        <#if errorMsg??>
            <div class="errorMsg" style="display:block;margin-bottom:16px;">${errorMsg}</div>
        </#if>

        <div class="cartLayout">
            <!-- LISTA PRODOTTI -->
            <div class="cartItems">
                <div class="formCard">
                    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;">
                        <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin:0;">Prodotti</h2>
                        <#if carrello?has_content>
                            <form method="post" action="${context}/cart" style="margin:0;">
                                <input type="hidden" name="action" value="clear">
                                <button type="submit" class="btnGray" style="padding:6px 14px;font-size:13px;"
                                        onclick="return confirm('Svuotare il carrello?')">
                                    <span class="material-symbols-outlined" style="font-size:16px;">delete</span> Svuota
                                </button>
                            </form>
                        </#if>
                    </div>

                    <#if carrelloVuoto>
                        <div class="emptyState">
                            <span class="material-symbols-outlined">shopping_cart</span>
                            <p>Il carrello è vuoto.</p>
                            <a href="${context}/menu" class="btnRed">Vai al menu</a>
                        </div>
                    <#else>
                        <#list carrello as item>
                            <div class="cartItemRow">
                                <img src="${context}/img/2.jpg" alt="${item.nomeProdotto}" class="cartItemImg">
                                <div class="cartItemInfo">
                                    <div class="cartItemName">${item.nomeProdotto}</div>
                                    <#if item.nomiCaratteristicheScelte?has_content>
                                        <div class="cartItemFeatures">
                                            Variazioni: ${item.nomiCaratteristicheScelte?join(", ")}
                                        </div>
                                    <#elseif item.caratteristicheScelte?has_content>
                                        <div class="cartItemFeatures">
                                            Caratteristiche ID: ${item.caratteristicheScelte?join(", ")}
                                        </div>
                                    </#if>
                                    <div class="cartItemPrice">€ ${item.prezzoUnitarioBase?string["0.00"]} cad. <small style="color:#888;display:inline-flex;align-items:center;gap:2px;"><span class="material-symbols-outlined" style="font-size:13px;">timer</span> ~${item.tempoPreparazione} min</small></div>
                                </div>
                                <div class="cartItemControls">
                                    <span class="qtyNum" style="font-size:16px;padding:0 12px;">x${item.quantita}</span>
                                </div>
                                <span class="cartItemTotal">€ ${item.subtotale?string["0.00"]}</span>
                                <form method="post" action="${context}/cart" style="margin:0;">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="index" value="${item?index}">
                                    <button type="submit" class="cartItemRemove" title="Rimuovi">
                                        <span class="material-symbols-outlined" style="font-size:20px;">close</span>
                                    </button>
                                </form>
                            </div>
                        </#list>
                    </#if>
                </div>
            </div>

            <!-- RIEPILOGO -->
            <div class="cartSummary">
                <div class="summaryCard">
                    <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin:0 0 14px;">Riepilogo</h2>
                    <#if carrelloVuoto>
                        <div style="text-align:center;color:#aaa;font-family:'Courier New',monospace;padding:20px 0;">
                            Aggiungi prodotti al carrello per vedere il riepilogo.
                        </div>
                    <#else>
                        <div class="summaryRow">
                            <span>Subtotale</span>
                            <strong>€ ${totale}</strong>
                        </div>
                        <div class="summaryRow">
                            <span>Consegna</span>
                            <strong style="color:#2d6a4f;">Gratuita</strong>
                        </div>
                        <div class="summaryRowTotal">
                            <span>Totale</span>
                            <strong>€ ${totale}</strong>
                        </div>

                        <div style="background:#f0f8ff;border-left:4px solid #3498db;padding:10px 12px;margin-top:14px;font-family:'Courier New',monospace;font-size:13px;color:#023e8a;border-radius:4px;">
                            <span class="material-symbols-outlined" style="font-size:16px;vertical-align:middle;">timer</span>
                            <strong>Tempo Prep. stimato:</strong> ~${tempoPrepTotale} min
                        </div>

                        <form method="post" action="${context}/cart" style="margin-top:16px;">
                            <input type="hidden" name="action" value="confirm">
                            <div class="formGroup" style="margin-bottom:12px;">
                                <label style="font-size:13px;font-weight:bold;display:block;margin-bottom:6px;">Orario di Consegna Preferito *</label>
                                <input type="time" name="orarioConsegna" required style="width:100%;padding:10px;border:1px solid #ccc;border-radius:6px;font-family:'Courier New',monospace;font-size:15px;box-sizing:border-box;">
                                <small style="color:#666;font-size:11px;display:block;margin-top:4px;">Rispettare tempo di prep. minimo (${tempoPrepTotale} min) e chiusura (23:30)</small>
                            </div>
                            <button type="submit" class="btnRed" style="width:100%;justify-content:center;padding:12px;">
                                <span class="material-symbols-outlined">check_circle</span> Conferma Ordine
                            </button>
                        </form>
                    </#if>
                </div>
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
