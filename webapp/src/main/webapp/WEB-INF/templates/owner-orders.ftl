<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Dashboard proprietario – Fooody: gestione di tutti gli ordini.">
    <title>Ordini – Fooody Owner</title>
    <link rel="stylesheet" href="${context}/style.css">
    <link rel="stylesheet" href="${context}/fonts.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
</head>
<body class="adminBody">

    <!-- SIDEBAR -->
    <div class="adminSidebar" id="adminSidebar">
        <div class="adminBrand">
            <span class="adminBrandText">Fooody</span>
        </div>
        <nav class="adminNav">
            <a href="${context}/owner-orders" class="adminNavLink active">
                <span class="material-symbols-outlined">list_alt</span> Tutti gli Ordini
            </a>
            <a href="${context}/owner-menu" class="adminNavLink">
                <span class="material-symbols-outlined">menu_book</span> Gestione Menu
            </a>
            <a href="${context}/owner-characteristics" class="adminNavLink">
                <span class="material-symbols-outlined">tune</span> Varianti e Gruppi
            </a>
            <a href="${context}/owner-ingredients" class="adminNavLink">
                <span class="material-symbols-outlined">kitchen</span> Gestione Ingredienti
            </a>
            <a href="${context}/owner-staff" class="adminNavLink">
                <span class="material-symbols-outlined">badge</span> Gestione Staff
            </a>
        </nav>
        <div class="adminNavBottom">
            <a href="${context}/index" class="adminNavLink">
                <span class="material-symbols-outlined">home</span> Sito pubblico
            </a>
            <a href="${context}/logout" class="adminNavLink">
                <span class="material-symbols-outlined">logout</span> Esci
            </a>
        </div>
    </div>

    <!-- OVERLAY MOBILE -->
    <div id="sidebarOverlay" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.4);z-index:99;"></div>

    <!-- CONTENUTO PRINCIPALE -->
    <div class="adminContent">
        <div class="adminTopbar">
            <button class="sidebarToggle" id="sidebarToggle">
                <span class="material-symbols-outlined">menu</span>
            </button>
            <h1 class="adminTopbarTitle">Tutti gli Ordini</h1>
            <div class="adminUserInfo">
                <span class="material-symbols-outlined" style="font-size:16px;color:#ca0000;vertical-align:middle;">manage_accounts</span>
                ${utenteLoggato.nome} <em style="color:#aaa;">(Proprietario)</em>
            </div>
            <a href="${context}/logout" class="btnGray" style="padding:6px 12px;font-size:13px;text-decoration:none;">
                <span class="material-symbols-outlined" style="font-size:16px;">logout</span> Esci
            </a>
        </div>

        <!-- Messaggi -->
        <#if successMsg??>
            <div class="successMsg" style="display:block;margin:10px 16px;">${successMsg}</div>
        </#if>
        <#if errorMsg??>
            <div class="errorMsg" style="display:block;margin:10px 16px;">${errorMsg}</div>
        </#if>

        <!-- FILTRI (form GET per PRG) -->
        <form method="get" action="${context}/owner-orders" class="adminFilters">
            <span style="font-size:13px;color:#888;">Filtra per stato:</span>
            <#assign statiList = [
                {"val":"", "label":"Tutti"},
                {"val":"inserito", "label":"Inseriti"},
                {"val":"in preparazione", "label":"In Prep."},
                {"val":"pronto", "label":"Pronti"},
                {"val":"in consegna", "label":"In Consegna"},
                {"val":"consegnato", "label":"Consegnati"},
                {"val":"annullato", "label":"Annullati"}
            ]>
            <#list statiList as s>
                <button type="submit" name="stato" value="${s.val}"
                        class="filterChip${(filtroStato == s.val)?then(' active','')}">
                    ${s.label}
                </button>
            </#list>
            <div style="margin-left:auto;display:flex;gap:8px;align-items:center;">
                <input type="date" name="dataInserimento" value="${filtroData!''}"
                       style="padding:6px 10px;border:1px solid #ddd;border-radius:6px;font-family:'Courier New',monospace;font-size:13px;"
                       onchange="this.form.submit()">
                <a href="${context}/owner-orders?dataInserimento=" class="btnGray" style="padding:6px 10px;font-size:13px;text-decoration:none;display:inline-flex;align-items:center;" title="Mostra tutte le date">
                    <span class="material-symbols-outlined" style="font-size:14px;">close</span>
                </a>
            </div>
        </form>

        <!-- TABELLA ORDINI -->
        <div class="adminTableWrap" style="margin-top:16px;">
            <#if ordini?has_content>
                <table class="adminTable" id="ordersTable">
                    <thead>
                        <tr>
                            <th>#Ordine</th>
                            <th>Inserimento</th>
                            <th>Consegna richiesta</th>
                            <th>Piatti</th>
                            <th>Totale</th>
                            <th>Stato</th>
                            <th>Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list ordini as ordine>
                            <tr>
                                <td data-label="#Ordine"><strong>#${ordine.idOrdine}</strong></td>
                                <td data-label="Inserimento">
                                    <span style="font-family:'Courier New',monospace;font-size:13px;">
                                        ${ordine.timeInserimento!"–"}
                                    </span>
                                </td>
                                <td data-label="Consegna">
                                    <span style="font-family:'Courier New',monospace;font-size:13px;">
                                        ${ordine.orarioConsegnaRichiesto!ordine.OrarioConsegnaRichiesto!"–"}
                                    </span>
                                </td>
                                <td data-label="Piatti">
                                    <#if ordine.dettagli?has_content>
                                        <div style="font-size:12px;max-width:220px;">
                                            <#list ordine.dettagli as det>
                                                <div><strong>${det.quantita}x</strong> ${det.nomeProdotto}</div>
                                                <#if det.caratteristiche?has_content>
                                                    <div style="font-size:10px;color:#ca0000;">(<#list det.caratteristiche as c>${c}<#sep>, </#list>)</div>
                                                </#if>
                                            </#list>
                                        </div>
                                    <#else>
                                        <span style="color:#aaa;font-size:12px;">–</span>
                                    </#if>
                                </td>
                                <td data-label="Totale">
                                    <#if ordine.prezzoTotale gt 0>
                                        <strong>€ ${ordine.prezzoTotale?string["0.00"]}</strong>
                                    <#else>
                                        –
                                    </#if>
                                </td>
                                <td data-label="Stato">
                                    <span class="statusBadge" style="background:${ordine.statoColore};">
                                        ${ordine.statoLabel}
                                    </span>
                                </td>
                                <td data-label="Azioni">
                                    <div style="display:flex;gap:6px;flex-wrap:wrap;">
                                        <button type="button" class="btnGray" style="padding:5px 10px;font-size:12px;background:#e9ecef;color:#333;border:1px solid #ccc;" onclick="openOrderModal(${ordine.idOrdine})">
                                            <span class="material-symbols-outlined" style="font-size:13px;">visibility</span> Dettagli
                                        </button>
                                        <#-- Azioni avanzamento stato basate sullo stato corrente -->
                                        <#assign prossimoStato = "">
                                        <#assign prossimoLabel = "">
                                        <#if ordine.statoCorrente == "inserito">
                                            <#assign prossimoStato = "in preparazione">
                                            <#assign prossimoLabel = "In Prep.">
                                        <#elseif ordine.statoCorrente == "in_preparazione" || ordine.statoCorrente == "in preparazione">
                                            <#assign prossimoStato = "pronto">
                                            <#assign prossimoLabel = "Pronto">
                                        <#elseif ordine.statoCorrente == "pronto">
                                            <#assign prossimoStato = "in consegna">
                                            <#assign prossimoLabel = "In Consegna">
                                        <#elseif ordine.statoCorrente == "in_consegna" || ordine.statoCorrente == "in consegna">
                                            <#assign prossimoStato = "consegnato">
                                            <#assign prossimoLabel = "Consegnato">
                                        </#if>

                                        <#if prossimoStato != "">
                                            <form method="post" action="${context}/owner-orders" style="margin:0;">
                                                <input type="hidden" name="action" value="advance">
                                                <input type="hidden" name="idOrdine" value="${ordine.idOrdine}">
                                                <input type="hidden" name="nuovoStato" value="${prossimoStato}">
                                                <button type="submit" class="btnRed" style="padding:5px 10px;font-size:12px;">
                                                    <span class="material-symbols-outlined" style="font-size:13px;">arrow_forward</span>
                                                    ${prossimoLabel}
                                                </button>
                                            </form>
                                        </#if>

                                        <#if ordine.statoCorrente != "consegnato" && ordine.statoCorrente != "annullato">
                                            <form method="post" action="${context}/owner-orders" style="margin:0;">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="idOrdine" value="${ordine.idOrdine}">
                                                <button type="submit" class="btnGray" style="padding:5px 10px;font-size:12px;"
                                                        onclick="return confirm('Annullare l\'ordine #${ordine.idOrdine}?')">
                                                    <span class="material-symbols-outlined" style="font-size:13px;">cancel</span> Annulla
                                                </button>
                                            </form>
                                        </#if>
                                    </div>
                                </td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            <#else>
                <div class="emptyState">
                    <span class="material-symbols-outlined">inbox</span>
                    <p>Nessun ordine trovato.</p>
                </div>
            </#if>
        </div>
    </div>

    <!-- MODALE DETTAGLI ORDINE ESTESO -->
    <div id="modalOrderDetails" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:300;align-items:center;justify-content:center;" onclick="if(event.target===this)closeOrderModal()">
        <div class="formCard" style="max-width:650px;width:95%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeOrderModal()" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 id="modOrdTitle" style="font-family:'Limelight',sans-serif;font-size:22px;margin-bottom:12px;">Dettagli Ordine</h2>
            <div id="modOrdMeta" style="background:#f8f9fa;padding:12px;border-radius:6px;font-family:'Courier New',monospace;font-size:13px;margin-bottom:16px;line-height:1.6;"></div>
            <h3 style="font-size:16px;margin-bottom:8px;border-bottom:1px solid #eee;padding-bottom:4px;">Piatti Ordinati</h3>
            <div id="modOrdItems"></div>
            <button type="button" class="btnGray" onclick="closeOrderModal()" style="width:100%;justify-content:center;margin-top:20px;">Chiudi</button>
        </div>
    </div>

    <script>
        var ORDINI_JSON = [
            <#if ordini?? && ordini?has_content>
                <#list ordini as o>
                {
                    "idOrdine": ${o.idOrdine},
                    "idCliente": ${o.idCliente!0},
                    "timeInserimento": "${(o.timeInserimento!"")?js_string}",
                    "orarioConsegna": "${(o.orarioConsegnaRichiesto!o.OrarioConsegnaRichiesto!"")?js_string}",
                    "prezzoTotale": ${o.prezzoTotale?c},
                    "statoLabel": "${(o.statoLabel!"")?js_string}",
                    "statoColore": "${(o.statoColore!"#999")?js_string}",
                    "dettagli": [
                        <#if o.dettagli?has_content>
                            <#list o.dettagli as d>
                            {
                                "nomeProdotto": "${(d.nomeProdotto!"")?js_string}",
                                "quantita": ${d.quantita},
                                "tempoPreparazione": ${d.tempoPreparazione!0},
                                "procedura": "${(d.procedura!"")?js_string}",
                                "caratteristiche": [
                                    <#if d.caratteristiche?has_content>
                                        <#list d.caratteristiche as c>"${c?js_string}"<#if c?has_next>,</#if></#list>
                                    </#if>
                                ]
                            }<#if d?has_next>,</#if>
                            </#list>
                        </#if>
                    ],
                    "operatori": [
                        <#if o.operatori?has_content>
                            <#list o.operatori as op>
                            {
                                "nome": "${(op.nome!"")?js_string}",
                                "cognome": "${(op.cognome!"")?js_string}"
                            }<#if op?has_next>,</#if>
                            </#list>
                        </#if>
                    ],
                    "storicoStati": [
                        <#if o.storicoStati?has_content>
                            <#list o.storicoStati as st>
                            {
                                "stato": "${(st.stato!st.nuovoStato!"")?js_string}",
                                "timestamp": "${(st.timestamp!st.data!"")?js_string}",
                                "operatore": "${(st.operatore!st.utente!"")?js_string}"
                            }<#if st?has_next>,</#if>
                            </#list>
                        </#if>
                    ]
                }<#if o?has_next>,</#if>
                </#list>
            </#if>
        ];

        function openOrderModal(id) {
            var o = ORDINI_JSON.find(function(x) { return x.idOrdine === id; });
            if (!o) return;
            document.getElementById('modOrdTitle').innerHTML = 'Ordine #' + o.idOrdine + ' <span class="statusBadge" style="background:' + o.statoColore + ';font-size:13px;vertical-align:middle;">' + o.statoLabel + '</span>';
            
            var metaHtml = '<strong>ID Cliente:</strong> ' + (o.idCliente ? '#' + o.idCliente : 'N/D') + '<br>' +
                           '<strong>Data/Ora Inserimento:</strong> ' + (o.timeInserimento || 'N/D') + '<br>' +
                           '<strong>Orario Consegna Richiesto:</strong> ' + (o.orarioConsegna || 'N/D') + '<br>' +
                           '<strong>Totale Ordine:</strong> <span style="color:#ca0000;font-weight:bold;font-size:15px;">€ ' + o.prezzoTotale.toFixed(2) + '</span>';
            
            if (o.operatori && o.operatori.length > 0) {
                var opsList = o.operatori.map(function(op){ return op.nome + ' ' + op.cognome; }).join(', ');
                metaHtml += '<br><strong style="color:#333;">Operatori coinvolti:</strong> ' + opsList;
            }

            metaHtml += '<hr style="margin:12px 0;border:none;border-top:1px dashed #ccc;"><strong style="color:#111;">Storico Stato Ordine:</strong><br>';
            if (o.storicoStati && o.storicoStati.length > 0) {
                metaHtml += '<ul style="margin:6px 0 0 16px;padding:0;font-size:13px;color:#444;">';
                o.storicoStati.forEach(function(st) {
                    metaHtml += '<li><strong>' + (st.stato || 'Cambio stato') + '</strong> ' + (st.timestamp ? ' il ' + st.timestamp : '') + (st.operatore ? ' (da ' + st.operatore + ')' : '') + '</li>';
                });
                metaHtml += '</ul>';
            } else {
                metaHtml += '<div style="font-size:13px;color:#555;margin-top:4px;"><span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle;">schedule</span> <strong>' + o.statoLabel + '</strong> impostato inizialmente il ' + (o.timeInserimento || 'N/D') + '</div>';
            }

            document.getElementById('modOrdMeta').innerHTML = metaHtml;

            var itemsHtml = '';
            if (o.dettagli && o.dettagli.length > 0) {
                itemsHtml += '<div style="display:flex;flex-direction:column;gap:10px;">';
                o.dettagli.forEach(function(d) {
                    itemsHtml += '<div style="border:1px solid #ddd;padding:10px;border-radius:6px;background:#fff;">';
                    itemsHtml += '<div style="display:flex;justify-content:space-between;font-weight:bold;font-size:15px;margin-bottom:4px;"><span>' + d.quantita + 'x ' + d.nomeProdotto + '</span>' + (d.tempoPreparazione > 0 ? '<span style="font-size:12px;color:#666;font-weight:normal;"><span class="material-symbols-outlined" style="font-size:13px;vertical-align:middle;">timer</span> ' + d.tempoPreparazione + ' min</span>' : '') + '</div>';
                    if (d.caratteristiche && d.caratteristiche.length > 0) {
                        itemsHtml += '<div style="font-size:12px;color:#ca0000;margin-bottom:6px;"><strong>Varianti/Caratteristiche:</strong> ' + d.caratteristiche.join(', ') + '</div>';
                    }
                    if (d.procedura) {
                        itemsHtml += '<div style="font-size:12px;background:#fffef0;border-left:3px solid #ffc107;padding:6px;color:#555;"><strong>Note Cucina:</strong> ' + d.procedura + '</div>';
                    }
                    itemsHtml += '</div>';
                });
                itemsHtml += '</div>';
            } else {
                itemsHtml = '<p style="color:#888;font-style:italic;">Nessun dettaglio disponibile per questo ordine.</p>';
            }
            document.getElementById('modOrdItems').innerHTML = itemsHtml;
            document.getElementById('modalOrderDetails').style.display = 'flex';
        }

        function closeOrderModal() {
            document.getElementById('modalOrderDetails').style.display = 'none';
        }

        var sidebar = document.getElementById('adminSidebar');
        var overlay = document.getElementById('sidebarOverlay');
        document.getElementById('sidebarToggle').addEventListener('click', function() {
            sidebar.classList.toggle('open');
            overlay.style.display = sidebar.classList.contains('open') ? 'block' : 'none';
        });
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('open');
            overlay.style.display = 'none';
        });
    </script>
</body>
</html>
