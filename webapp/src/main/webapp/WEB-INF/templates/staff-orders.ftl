<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no">
    <meta name="description" content="Ordini correnti – Fooody Staff: gestisci e aggiorna gli stati degli ordini.">
    <title>Ordini – Fooody Staff</title>
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
            <a href="${context}/staff-orders" class="adminNavLink active">
                <span class="material-symbols-outlined">list_alt</span> Ordini Correnti
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

    <!-- CONTENUTO -->
    <div class="adminContent">
        <div class="adminTopbar">
            <button class="sidebarToggle" id="sidebarToggle">
                <span class="material-symbols-outlined">menu</span>
            </button>
            <h1 class="adminTopbarTitle">Ordini Correnti</h1>
            <div class="adminUserInfo">
                <span class="material-symbols-outlined" style="font-size:16px;color:#ca0000;vertical-align:middle;">badge</span>
                ${utenteLoggato.nome}
                <em style="color:#aaa;">
                    (${(utenteLoggato.ruolo == "proprietario")?then("Proprietario","Personale")})
                </em>
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
        <form method="get" action="${context}/staff-orders" class="adminFilters">
            <span style="font-size:13px;color:#888;">Filtra:</span>
            <#assign filtri = [
                {"val":"all","label":"Tutti"},
                {"val":"inserito","label":"Inseriti"},
                {"val":"in preparazione","label":"In Prep."},
                {"val":"pronto","label":"Pronti"},
                {"val":"in consegna","label":"In Consegna"}
            ]>
            <#list filtri as f>
                <button type="submit" name="filtro" value="${f.val}"
                        class="filterChip${(filtroStato == f.val)?then(' active','')}">
                    ${f.label}
                </button>
            </#list>
            <div style="margin-left:auto;display:flex;gap:8px;align-items:center;">
                <input type="date" name="dataInserimento" value="${filtroData!''}"
                       style="padding:6px 10px;border:1px solid #ddd;border-radius:6px;font-family:'Courier New',monospace;font-size:13px;"
                       onchange="this.form.submit()">
                <a href="${context}/staff-orders?dataInserimento=" class="btnGray" style="padding:6px 10px;font-size:13px;text-decoration:none;display:inline-flex;align-items:center;" title="Mostra tutte le date">
                    <span class="material-symbols-outlined" style="font-size:14px;">close</span>
                </a>
            </div>
        </form>

        <!-- GRIGLIA ORDINI -->
        <div class="ordersGrid">
            <#if ordini?has_content>
                <#list ordini as ordine>
                    <#-- Calcola urgency class e prossimo stato -->
                    <#assign urgencyClass = "">
                    <#if ordine.statoCorrente == "inserito"><#assign urgencyClass = "urgentRed">
                    <#elseif ordine.statoCorrente == "in_preparazione" || ordine.statoCorrente == "in preparazione"><#assign urgencyClass = "urgentYellow">
                    <#elseif ordine.statoCorrente == "pronto"><#assign urgencyClass = "urgentBlue">
                    <#elseif ordine.statoCorrente == "in_consegna" || ordine.statoCorrente == "in consegna"><#assign urgencyClass = "urgentGreen">
                    </#if>

                    <#assign prossimoStato = "">
                    <#assign prossimoLabel = "">
                    <#if ordine.statoCorrente == "inserito">
                        <#assign prossimoStato = "in preparazione"><#assign prossimoLabel = "In Preparazione">
                    <#elseif ordine.statoCorrente == "in_preparazione" || ordine.statoCorrente == "in preparazione">
                        <#assign prossimoStato = "pronto"><#assign prossimoLabel = "Pronto">
                    <#elseif ordine.statoCorrente == "pronto">
                        <#assign prossimoStato = "in consegna"><#assign prossimoLabel = "In Consegna">
                    <#elseif ordine.statoCorrente == "in_consegna" || ordine.statoCorrente == "in consegna">
                        <#assign prossimoStato = "consegnato"><#assign prossimoLabel = "Consegnato">
                    </#if>

                    <div class="staffOrderCard ${urgencyClass}">
                        <div class="staffCardHeader">
                            <span class="orderIdLabel" style="font-size:16px;">#${ordine.idOrdine}</span>
                            <span class="statusBadge" style="background:${ordine.statoColore};">
                                ${ordine.statoLabel}
                            </span>
                        </div>

                        <div class="staffCardMeta">
                            <span class="material-symbols-outlined" style="font-size:15px;vertical-align:middle;">schedule</span>
                            Inserito: ${ordine.timeInserimento!"–"}
                        </div>
                        <#if ordine.orarioConsegnaRichiesto??>
                            <div class="staffCardMeta">
                                <span class="material-symbols-outlined" style="font-size:15px;vertical-align:middle;">local_shipping</span>
                                Consegna: ${ordine.orarioConsegnaRichiesto}
                            </div>
                        </#if>
                        <#if ordine.prezzoTotale gt 0>
                            <div class="staffCardMeta">
                                <span class="material-symbols-outlined" style="font-size:15px;vertical-align:middle;">payments</span>
                                € ${ordine.prezzoTotale?string["0.00"]}
                            </div>
                        </#if>

                        <#-- Lista Piatti per Cucina / Staff -->
                        <#if ordine.dettagli?has_content>
                            <div style="margin:10px 0;border-top:1px dashed #ccc;padding-top:8px;">
                                <div style="font-family:'Courier New',monospace;font-size:11px;font-weight:bold;color:#444;text-transform:uppercase;margin-bottom:4px;display:flex;align-items:center;gap:4px;">
                                    <span class="material-symbols-outlined" style="font-size:14px;">restaurant_menu</span> Piatti da preparare:
                                </div>
                                <#list ordine.dettagli as det>
                                    <div style="background:#fdfdfd;border:1px solid #eee;border-radius:6px;padding:6px 8px;margin-bottom:6px;font-size:13px;">
                                        <div style="display:flex;justify-content:space-between;font-weight:bold;">
                                            <span>${det.quantita}x ${det.nomeProdotto}</span>
                                            <#if det.tempoPreparazione??>
                                                <span style="font-size:11px;color:#888;display:inline-flex;align-items:center;gap:2px;"><span class="material-symbols-outlined" style="font-size:12px;">timer</span> ${det.tempoPreparazione}m</span>
                                            </#if>
                                        </div>
                                        <#if det.caratteristiche?has_content>
                                            <div style="font-size:11px;color:#ca0000;margin-top:2px;display:flex;align-items:center;gap:2px;">
                                                <span class="material-symbols-outlined" style="font-size:12px;">tune</span> <#list det.caratteristiche as c>${c}<#sep>, </#list>
                                            </div>
                                        </#if>
                                        <#if det.procedura?has_content>
                                            <div style="font-size:11px;color:#555;background:#f5f5f5;padding:4px;border-radius:4px;margin-top:4px;display:flex;align-items:center;gap:4px;">
                                                <span class="material-symbols-outlined" style="font-size:13px;">soup_kitchen</span> <em>${det.procedura}</em>
                                            </div>
                                        </#if>
                                    </div>
                                </#list>
                            </div>
                        </#if>

                        <div class="staffCardActions">
                            <#if prossimoStato != "">
                                <form method="post" action="${context}/staff-orders" style="margin:0;">
                                    <input type="hidden" name="action" value="advance">
                                    <input type="hidden" name="idOrdine" value="${ordine.idOrdine}">
                                    <input type="hidden" name="nuovoStato" value="${prossimoStato}">
                                    <button type="submit" class="btnRed" style="padding:7px 12px;font-size:13px;">
                                        <span class="material-symbols-outlined" style="font-size:15px;">arrow_forward</span>
                                        ${prossimoLabel}
                                    </button>
                                </form>
                            <#else>
                                <div class="successMsg" style="display:inline-flex;align-items:center;gap:4px;font-size:13px;">
                                    <span class="material-symbols-outlined" style="font-size:14px;">check</span> Ordine completato
                                </div>
                            </#if>
                        </div>
                    </div>
                </#list>
            <#else>
                <div class="emptyState" style="width:100%;">
                    <span class="material-symbols-outlined">check_circle</span>
                    <p>Nessun ordine attivo al momento.</p>
                </div>
            </#if>
        </div>
    </div>

    <script>
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

        // Auto-refresh ogni 15 secondi per vedere nuovi ordini
        setTimeout(function() { location.reload(); }, 15000);
    </script>
</body>
</html>
