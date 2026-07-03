<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="I miei ordini – Fooody: storico e tracking degli ordini.">
    <title>I miei Ordini – Fooody</title>
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
            <div><a href="${context}/orders" style="color:#ca0000;font-weight:bold;">I miei ordini</a></div>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
            <a href="${context}/cart" class="navCartLink nav-extra">
                <span class="material-symbols-outlined">shopping_cart</span>
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
                <h1 class="pageTitle">I miei <span>Ordini</span></h1>
                <p class="pageSubtitle">Traccia gli ordini in corso e visualizza lo storico.</p>
            </div>
            <a href="${context}/menu" class="btnRed">
                <span class="material-symbols-outlined">add</span> Nuovo ordine
            </a>
        </div>

        <#-- Messaggi di feedback e notifiche email simulate -->
        <#if simulatedEmail??>
            <div style="background:#e8f5e9;border:1px solid #2e7d32;color:#1b5e20;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-family:'Courier New',monospace;font-size:13px;">
                ${simulatedEmail}
            </div>
        </#if>
        <#if deliveryEmailAlert??>
            <div style="background:#e3f2fd;border:1px solid #1565c0;color:#0d47a1;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-family:'Courier New',monospace;font-size:13px;">
                ${deliveryEmailAlert}
            </div>
        </#if>
        <#if successMsg??>
            <div class="successMsg" style="display:block;margin-bottom:16px;">${successMsg}</div>
        </#if>
        <#if errorMsg??>
            <div class="errorMsg" style="display:block;margin-bottom:16px;">${errorMsg}</div>
        </#if>

        <#-- ORDINI IN CORSO -->
        <h2 style="font-family:'Limelight',sans-serif;font-size:22px;margin-bottom:12px;">
            <span class="material-symbols-outlined" style="vertical-align:middle;font-size:22px;">local_shipping</span>
            Ordini in corso
        </h2>

        <#assign ordiniAttivi = []>
        <#list ordini as _o>
            <#assign st = (_o.statoCorrente!"")?lower_case?trim>
            <#if st != "consegnato" && st != "annullato">
                <#assign ordiniAttivi = ordiniAttivi + [_o]>
            </#if>
        </#list>
        <#if ordiniAttivi?has_content>
            <#list ordiniAttivi as ordine>
                <#assign normStato = (ordine.statoCorrente!"")?lower_case?trim?replace(" ", "_")>
                <div class="orderTrackCard">
                    <div class="orderCardHeader">
                        <div>
                            <div class="orderIdLabel">#${ordine.idOrdine}</div>
                            <#if (ordine.orarioConsegnaRichiesto?? || ordine.OrarioConsegnaRichiesto??)>
                                <div class="orderDateLabel">Consegna: ${ordine.orarioConsegnaRichiesto!ordine.OrarioConsegnaRichiesto}</div>
                            </#if>
                        </div>
                        <div style="display:flex;gap:8px;align-items:center;">
                            <span class="statusBadge" style="background:${ordine.statoColore};">
                                ${ordine.statoLabel}
                            </span>
                            <#if ordine.prezzoTotale gt 0>
                                <strong style="font-family:'Courier New',monospace;">€ ${ordine.prezzoTotale?string["0.00"]}</strong>
                            </#if>
                        </div>
                    </div>

                    <#-- Tracker stati -->
                    <div class="trackSteps">
                        <#assign statiSeq = ["inserito","in_preparazione","pronto","in_consegna","consegnato"]>
                        <#assign statiLabel = {"inserito":"Inserito","in_preparazione":"In Prep.","pronto":"Pronto","in_consegna":"In Consegna","consegnato":"Consegnato"}>
                        <#assign curIdx = statiSeq?seq_index_of(normStato)>
                        <#list statiSeq as stato>
                            <#assign i = stato?index>
                            <#assign isDone = (i < curIdx)>
                            <#assign isActive = (i == curIdx)>
                            <#if i gt 0>
                                <div class="trackLine<#if isDone> done</#if>"></div>
                            </#if>
                            <div class="trackStep <#if isDone>done<#elseif isActive>active</#if>">
                                <div class="trackDot"><#if isDone><span class="material-symbols-outlined" style="font-size:14px;color:white;">check</span><#elseif isActive>●<#else>○</#if></div>
                                ${statiLabel[stato]!""}
                            </div>
                        </#list>
                    </div>

                    <#if ordine.dettagli?has_content>
                        <div style="background:#FFF6ED;border-radius:8px;padding:12px 14px;margin:14px 0;border:1px solid #ede7df;">
                            <div style="font-size:12px;text-transform:uppercase;color:#888;font-weight:bold;margin-bottom:8px;">Contenuto dell'ordine:</div>
                            <ul style="margin:0;padding-left:18px;list-style-type:disc;">
                                <#list ordine.dettagli as det>
                                    <li style="margin-bottom:6px;font-size:14px;color:#333;">
                                        <strong>${det.quantita}x ${det.nomeProdotto!""}</strong>
                                        <#if det.caratteristiche?has_content>
                                            <span style="color:#666;font-size:13px;">(${det.caratteristiche?join(", ")})</span>
                                        </#if>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                    </#if>

                    <div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px;flex-wrap:wrap;gap:10px;">
                        <div style="font-family:'Courier New',monospace;font-size:13px;color:#555;">
                            Ordine inserito il: ${ordine.timeInserimento!"–"}
                        </div>
                        <#if normStato == "inserito">
                            <form method="post" action="${context}/orders" style="margin:0;">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="idOrdine" value="${ordine.idOrdine}">
                                <button type="submit" class="btnOutline" style="padding:6px 12px;font-size:12px;color:#ca0000;border-color:#ca0000;cursor:pointer;" onclick="return confirm('Sei sicuro di voler annullare l\'ordine #${ordine.idOrdine}?')">
                                    <span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle;">cancel</span> Annulla Ordine
                                </button>
                            </form>
                        </#if>
                    </div>
                </div>
            </#list>
        <#else>
            <div class="emptyState">
                <span class="material-symbols-outlined">local_shipping</span>
                <p>Nessun ordine in corso.</p>
                <a href="${context}/menu" class="btnRed">Ordina ora</a>
            </div>
        </#if>

        <hr style="margin:28px 0;">

        <#-- STORICO ORDINI -->
        <h2 style="font-family:'Limelight',sans-serif;font-size:22px;margin-bottom:12px;">
            <span class="material-symbols-outlined" style="vertical-align:middle;font-size:22px;">history</span>
            Storico ordini
        </h2>

        <#assign ordiniStorico = []>
        <#list ordini as _o>
            <#assign st = (_o.statoCorrente!"")?lower_case?trim>
            <#if st == "consegnato" || st == "annullato">
                <#assign ordiniStorico = ordiniStorico + [_o]>
            </#if>
        </#list>
        <#if ordiniStorico?has_content>
            <#list ordiniStorico as ordine>
                <div class="orderHistoryRow" style="flex-wrap:wrap;">
                    <div style="flex:1;min-width:200px;">
                        <div class="orderIdLabel" style="font-size:16px;">#${ordine.idOrdine}</div>
                        <div class="orderDateLabel">${ordine.timeInserimento!"–"}</div>
                    </div>
                    <div style="text-align:right;">
                        <span class="statusBadge" style="background:${ordine.statoColore};">${ordine.statoLabel}</span>
                        <#if ordine.prezzoTotale gt 0>
                            <div style="font-family:'Courier New',monospace;font-weight:bold;margin-top:6px;">
                                € ${ordine.prezzoTotale?string["0.00"]}
                            </div>
                        </#if>
                    </div>
                    <#if ordine.dettagli?has_content>
                        <div style="width:100%;background:#FFF6ED;border-radius:6px;padding:10px 12px;margin-top:12px;border:1px solid #ede7df;">
                            <ul style="margin:0;padding-left:18px;list-style-type:disc;">
                                <#list ordine.dettagli as det>
                                    <li style="margin-bottom:4px;font-size:13px;color:#444;">
                                        <strong>${det.quantita}x ${det.nomeProdotto!""}</strong>
                                        <#if det.caratteristiche?has_content>
                                            <span style="color:#666;">(${det.caratteristiche?join(", ")})</span>
                                        </#if>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                    </#if>
                </div>
            </#list>
        <#else>
            <div class="emptyState">
                <span class="material-symbols-outlined">history</span>
                <p>Nessun ordine completato o annullato finora.</p>
            </div>
        </#if>

        <#if !ordini?has_content && !errorMsg??>
            <div class="emptyState" style="margin-top:32px;">
                <span class="material-symbols-outlined">receipt_long</span>
                <p>Non hai ancora effettuato ordini.</p>
                <a href="${context}/menu" class="btnRed">Vai al menu</a>
            </div>
        </#if>
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
