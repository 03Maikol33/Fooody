<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Gestione Menu – Fooody Owner: aggiungi e modifica prodotti e caratteristiche.">
    <title>Gestione Menu – Fooody Owner</title>
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
            <a href="${context}/owner-orders" class="adminNavLink">
                <span class="material-symbols-outlined">list_alt</span> Tutti gli Ordini
            </a>
            <a href="${context}/owner-menu" class="adminNavLink active">
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

    <!-- CONTENUTO -->
    <div class="adminContent">
        <div class="adminTopbar">
            <button class="sidebarToggle" id="sidebarToggle">
                <span class="material-symbols-outlined">menu</span>
            </button>
            <h1 class="adminTopbarTitle">Menu</h1>
            <div style="margin-left:auto;margin-right:16px;">
                <button type="button" class="btnRed" onclick="openModal('modalAddProd')" style="padding:8px 16px;font-size:13px;">
                    <span class="material-symbols-outlined">add</span>Prodotto
                </button>
            </div>
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

        <!-- FILTRI CATEGORIA -->
        <div class="filterBar" style="padding:12px 16px;margin:0;">
            <a href="${context}/owner-menu" class="filterChip<#if filtroCategoria == ''> active</#if>">Tutti</a>
            <#if categorieList??>
                <#list categorieList as cat>
                    <#if cat?has_content>
                        <a href="${context}/owner-menu?categoria=${cat?url('UTF-8')}"
                           class="filterChip<#if filtroCategoria == cat> active</#if>">
                            ${cat}
                        </a>
                    </#if>
                </#list>
            </#if>
        </div>

        <!-- GRIGLIA PRODOTTI -->
        <div style="padding:16px;">
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="formCard" style="margin-bottom:16px;">
                        <div style="display:flex;gap:16px;align-items:flex-start;flex-wrap:wrap;">
                            <img src="${context}/img/2.jpg" alt="${prodotto.nome}"
                                 style="width:80px;height:80px;object-fit:cover;border-radius:10px;flex-shrink:0;">
                            <div style="flex:1;min-width:200px;">
                                <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:8px;">
                                    <div>
                                        <span class="menuCardCategory">${prodotto.categoria!"–"}</span>
                                        <h3 style="font-family:'Limelight',sans-serif;font-size:18px;margin:4px 0;">
                                            ${prodotto.nome}
                                        </h3>
                                        <p style="font-family:'Courier New',monospace;font-size:13px;color:#555;margin:0 0 8px;">
                                            ${prodotto.descrizione!""}
                                        </p>
                                        <#if prodotto.procedura?has_content>
                                            <div style="font-family:'Courier New',monospace;font-size:11px;color:#777;background:#f9f9f9;padding:6px;border-radius:4px;margin-bottom:8px;">
                                                <em>${prodotto.procedura}</em>
                                            </div>
                                        </#if>
                                    </div>
                                    <div style="text-align:right; flex-wrap:wrap;">
                                        <div style="font-family:'Courier New',monospace;font-size:20px;color:#ca0000;font-weight:bold;">
                                            € ${prodotto.prezzoBase?string["0.00"]}
                                        </div>
                                        <#if prodotto.tempoPreparazione??>
                                            <div style="font-family:'Courier New',monospace;font-size:12px;color:#888;">
                                                ⏱ ${prodotto.tempoPreparazione} min
                                            </div>
                                        </#if>
                                        <div style="display:flex;flex-wrap: wrap; gap:6px;justify-content:flex-end;margin-top:8px;">
                                            <button type="button" class="btnOutline" style="padding:4px 8px;font-size:11px;"
                                                    onclick="openEditProdModal('${prodotto.idProdotto}', '${prodotto.nome?js_string}', '${prodotto.categoria?js_string}', '${prodotto.descrizione?js_string}', '${prodotto.prezzoBase?string["0.00"]}', '${prodotto.tempoPreparazione!"15"}', '${prodotto.procedura?js_string!""}')">
                                                <span class="material-symbols-outlined" style="font-size:14px;">edit</span> Modifica
                                            </button>
                                            <button type="button" class="btnRed" style="padding:4px 8px;font-size:11px;"
                                                    onclick="openAddFeatModal('${prodotto.idProdotto}', '${prodotto.nome?js_string}')">
                                                <span class="material-symbols-outlined" style="font-size:14px;">add_circle</span>Varianti/Gruppi
                                            </button>
                                            <button type="button" class="btnRed" style="padding:4px 8px;font-size:11px;background:#ff8c00;border-color:#ff8c00;"
                                                    onclick="openAddIngModal('${prodotto.idProdotto}', '${prodotto.nome?js_string}')">
                                                <span class="material-symbols-outlined" style="font-size:14px;">kitchen</span>Ingredienti
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <#-- Caratteristiche del prodotto -->
                                <#if prodotto.caratteristiche?has_content>
                                    <div style="margin-top:8px;">
                                        <div style="font-family:'Courier New',monospace;font-size:12px;font-weight:bold;color:#333;margin-bottom:6px;text-transform:uppercase;letter-spacing:0.5px;">
                                            Caratteristiche:
                                        </div>
                                        <div style="display:flex;flex-wrap:wrap;gap:6px;">
                                            <#list prodotto.caratteristiche as car>
                                                <span style="display:inline-flex;align-items:center;gap:4px;background:#f5f5f5;border:1px solid #ddd;border-radius:20px;padding:4px 10px;font-family:'Courier New',monospace;font-size:12px;">
                                                    ${car.nome}
                                                    <#if car.differenzaPrezzo != 0>
                                                        <span style="color:${(car.differenzaPrezzo > 0)?then('#2d6a4f','#ca0000')};font-weight:bold;">
                                                            ${(car.differenzaPrezzo > 0)?then('+','')}${car.differenzaPrezzo?string["0.00"]}€
                                                        </span>
                                                    </#if>
                                                    <#if car.isDefault>
                                                        <span class="statusBadge" style="background:#2d6a4f;font-size:9px;padding:1px 5px;">default</span>
                                                    </#if>
                                                    <form method="post" action="${context}/owner-menu" style="display:inline;margin:0;">
                                                        <input type="hidden" name="action" value="removeCaratteristica">
                                                        <input type="hidden" name="idProdotto" value="${prodotto.idProdotto}">
                                                        <input type="hidden" name="idCaratteristica" value="${car.idCaratteristica}">
                                                        <button type="submit" title="Rimuovi caratteristica"
                                                                onclick="return confirm('Rimuovere la caratteristica \'${car.nome}\' dal prodotto?')"
                                                                style="background:none;border:none;cursor:pointer;color:#ca0000;padding:0;margin-left:2px;line-height:1;">
                                                            <span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle;">close</span>
                                                        </button>
                                                    </form>
                                                </span>
                                            </#list>
                                        </div>
                                    </div>
                                <#else>
                                    <p style="font-family:'Courier New',monospace;font-size:12px;color:#aaa;margin-top:8px;">
                                        Nessuna caratteristica configurata.
                                    </p>
                                </#if>

                                <#-- Ingredienti del prodotto -->
                                <#if prodotto.ingredienti?has_content>
                                    <div style="margin-top:12px;border-top:1px dashed #eee;padding-top:8px;">
                                        <div style="font-family:'Courier New',monospace;font-size:12px;font-weight:bold;color:#e65100;margin-bottom:6px;text-transform:uppercase;letter-spacing:0.5px;">
                                            Ingredienti Ricetta:
                                        </div>
                                        <div style="display:flex;flex-wrap:wrap;gap:6px;">
                                            <#list prodotto.ingredienti as ing>
                                                <span style="display:inline-flex;align-items:center;gap:4px;background:#fff8e1;border:1px solid #ffe0b2;border-radius:20px;padding:4px 10px;font-family:'Courier New',monospace;font-size:12px;color:#e65100;">
                                                    ${ing.nome} <#if ing.quantita??><b>(${ing.quantita})</b></#if>
                                                    <form method="post" action="${context}/owner-menu" style="display:inline;margin:0;">
                                                        <input type="hidden" name="action" value="removeIngrediente">
                                                        <input type="hidden" name="idProdotto" value="${prodotto.idProdotto}">
                                                        <input type="hidden" name="idIngrediente" value="${ing.idIngrediente}">
                                                        <button type="submit" title="Rimuovi ingrediente"
                                                                onclick="return confirm('Rimuovere l\'ingrediente \'${ing.nome}\' dal prodotto?')"
                                                                style="background:none;border:none;cursor:pointer;color:#ca0000;padding:0;margin-left:2px;line-height:1;">
                                                            <span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle;">close</span>
                                                        </button>
                                                    </form>
                                                </span>
                                            </#list>
                                        </div>
                                    </div>
                                <#else>
                                    <p style="font-family:'Courier New',monospace;font-size:12px;color:#aaa;margin-top:8px;">
                                        Nessun ingrediente associato.
                                    </p>
                                </#if>
                            </div>
                        </div>
                    </div>
                </#list>
            <#else>
                <div class="emptyState">
                    <span class="material-symbols-outlined">menu_book</span>
                    <p>Nessun prodotto trovato.</p>
                </div>
            </#if>
        </div>
    </div>

    <!-- MODAL AGGIUNGI PRODOTTO -->
    <div id="modalAddProd" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:500px;width:90%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeModal('modalAddProd')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Nuovo Prodotto</h2>
            <form method="post" action="${context}/owner-menu">
                <input type="hidden" name="action" value="addProdotto">
                <div class="formRow">
                    <div class="formGroup">
                        <label>Nome *</label>
                        <input type="text" name="nome" required placeholder="Es. Hamburger Special">
                    </div>
                    <div class="formGroup">
                        <label>Categoria *</label>
                        <input type="text" name="categoria" required placeholder="Es. Panini" list="categorieEsistenti">
                    </div>
                </div>
                <div class="formGroup">
                    <label>Descrizione *</label>
                    <input type="text" name="descrizione" required placeholder="Es. Manzo 200g, cheddar, bacon">
                </div>
                <div class="formRow">
                    <div class="formGroup">
                        <label>Prezzo Base (€) *</label>
                        <input type="number" step="0.01" name="prezzoBase" required placeholder="10.50">
                    </div>
                    <div class="formGroup">
                        <label>Tempo Prep. (min) *</label>
                        <input type="number" name="tempoPreparazione" required value="15">
                    </div>
                </div>
                <div class="formGroup">
                    <label>Procedura per cucina</label>
                    <input type="text" name="procedura" placeholder="Es. 1. Cuocere carne a puntino. 2. Assemblare.">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Salva Prodotto</button>
            </form>
        </div>
    </div>

    <!-- MODAL MODIFICA PRODOTTO -->
    <div id="modalEditProd" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:500px;width:90%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeModal('modalEditProd')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Modifica Prodotto</h2>
            <form method="post" action="${context}/owner-menu">
                <input type="hidden" name="action" value="updateProdotto">
                <input type="hidden" name="idProdotto" id="epId">
                <div class="formRow">
                    <div class="formGroup">
                        <label>Nome *</label>
                        <input type="text" name="nome" id="epNome" required>
                    </div>
                    <div class="formGroup">
                        <label>Categoria *</label>
                        <input type="text" name="categoria" id="epCat" required list="categorieEsistenti">
                    </div>
                </div>
                <div class="formGroup">
                    <label>Descrizione *</label>
                    <input type="text" name="descrizione" id="epDesc" required>
                </div>
                <div class="formRow">
                    <div class="formGroup">
                        <label>Prezzo Base (€) *</label>
                        <input type="number" step="0.01" name="prezzoBase" id="epPrezzo" required>
                    </div>
                    <div class="formGroup">
                        <label>Tempo Prep. (min) *</label>
                        <input type="number" name="tempoPreparazione" id="epTempo" required>
                    </div>
                </div>
                <div class="formGroup">
                    <label>Procedura per cucina</label>
                    <input type="text" name="procedura" id="epProc">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Aggiorna Prodotto</button>
            </form>
        </div>
    </div>

    <!-- MODAL AGGIUNGI CARATTERISTICA / GRUPPO -->
    <div id="modalAddFeat" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:480px;width:90%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeModal('modalAddFeat')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:6px;">Associa Varianti o Gruppi</h2>
            <p id="afProdName" style="font-family:'Courier New',monospace;font-size:13px;color:#666;margin-bottom:16px;"></p>
            <form method="post" action="${context}/owner-menu">
                <input type="hidden" name="action" value="assignExistingCaratteristica">
                <input type="hidden" name="idProdotto" id="afIdProd">
                <div class="formGroup">
                    <label>Seleziona Gruppo GMC o Variante *</label>
                    <select name="itemToAssign" required style="width:100%;padding:10px;border:1px solid #ccc;border-radius:6px;font-family:'Courier New',monospace;font-size:13px;background:#fff;">
                        <option value="">-- Seleziona un elemento esistente --</option>
                        <#if tuttiGruppi?has_content>
                            <optgroup label="Gruppi di Mutua Esclusione (GMC)">
                                <#list tuttiGruppi as g>
                                    <option value="GMC_${g.idGmc}">[GRUPPO] ${g.nome} (${g.descrizione!""})</option>
                                </#list>
                            </optgroup>
                        </#if>
                        <#if tutteCaratteristiche?has_content>
                            <optgroup label="Caratteristiche Svincolate / Singole">
                                <#list tutteCaratteristiche as c>
                                    <#if !c.gruppo?has_content && (c.idGmc!0) == 0>
                                        <option value="CHAR_${c.idCaratteristica}">${c.nome} (+€${c.differenzaPrezzo?string["0.00"]})</option>
                                    </#if>
                                </#list>
                            </optgroup>
                            <optgroup label="Altre Varianti (appartenenti a gruppi)">
                                <#list tutteCaratteristiche as c>
                                    <#if c.gruppo?has_content || ((c.idGmc!0) > 0)>
                                        <option value="CHAR_${c.idCaratteristica}">${c.nome} (+€${c.differenzaPrezzo?string["0.00"]}) [GMC ${c.idGmc!""}]</option>
                                    </#if>
                                </#list>
                            </optgroup>
                        </#if>
                    </select>
                </div>
                <div style="margin-top:12px;font-size:12px;font-family:'Courier New',monospace;color:#555;background:#f9f9f9;padding:8px;border-radius:4px;border:1px dashed #ccc;display:flex;gap:6px;align-items:flex-start;">
                    <span class="material-symbols-outlined" style="font-size:16px;color:#f59e0b;">lightbulb</span>
                    <div>
                        Selezionando un <b>Gruppo GMC</b> verranno associate in un colpo solo tutte le opzioni di quel gruppo al prodotto.<br>
                        Per creare nuove varianti o gruppi, vai alla sezione <a href="${context}/owner-characteristics" style="color:#d32f2f;font-weight:bold;text-decoration:underline;">Varianti e Gruppi</a>.
                    </div>
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:16px;">Associa al Prodotto</button>
            </form>
        </div>
    </div>

    <!-- MODAL AGGIUNGI INGREDIENTE -->
    <div id="modalAddIng" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:480px;width:90%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeModal('modalAddIng')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:6px;">Associa Ingrediente</h2>
            <p id="aiProdName" style="font-family:'Courier New',monospace;font-size:13px;color:#666;margin-bottom:16px;"></p>
            <form method="post" action="${context}/owner-menu">
                <input type="hidden" name="action" value="assignExistingIngrediente">
                <input type="hidden" name="idProdotto" id="aiIdProd">
                <div class="formGroup">
                    <label>Seleziona Ingrediente *</label>
                    <select name="idIngrediente" required style="width:100%;padding:10px;border:1px solid #ccc;border-radius:6px;font-family:'Courier New',monospace;font-size:13px;background:#fff;">
                        <option value="">-- Seleziona un ingrediente dal dizionario --</option>
                        <#if tuttiIngredienti?has_content>
                            <#list tuttiIngredienti as ing>
                                <option value="${ing.idIngrediente}">${ing.nome}</option>
                            </#list>
                        </#if>
                    </select>
                </div>
                <div class="formGroup">
                    <label>Quantità nella ricetta *</label>
                    <input type="text" name="quantita" required placeholder="Es. 150g, 2 fette, q.b., 1 cucchiaio...">
                </div>
                <div style="margin-top:12px;font-size:12px;font-family:'Courier New',monospace;color:#555;background:#f9f9f9;padding:8px;border-radius:4px;border:1px dashed #ccc;display:flex;gap:6px;align-items:flex-start;">
                    <span class="material-symbols-outlined" style="font-size:16px;color:#f59e0b;">lightbulb</span>
                    <div>
                        Per aggiungere nuovi ingredienti al dizionario globale, vai alla sezione <a href="${context}/owner-ingredients" style="color:#ff8c00;font-weight:bold;text-decoration:underline;">Gestione Ingredienti</a>.
                    </div>
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:16px;background:#ff8c00;border-color:#ff8c00;">Associa Ingrediente</button>
            </form>
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

        function openModal(id) { document.getElementById(id).style.display = 'flex'; }
        function closeModal(id) { document.getElementById(id).style.display = 'none'; }

        function openEditProdModal(id, nome, cat, desc, prezzo, tempo, proc) {
            document.getElementById('epId').value = id;
            document.getElementById('epNome').value = nome;
            document.getElementById('epCat').value = cat;
            document.getElementById('epDesc').value = desc;
            document.getElementById('epPrezzo').value = prezzo;
            document.getElementById('epTempo').value = tempo;
            document.getElementById('epProc').value = proc;
            openModal('modalEditProd');
        }

        function openAddFeatModal(idProd, nomeProd) {
            document.getElementById('afIdProd').value = idProd;
            document.getElementById('afProdName').textContent = 'Per prodotto: ' + nomeProd;
            openModal('modalAddFeat');
        }

        function openAddIngModal(idProd, nomeProd) {
            document.getElementById('aiIdProd').value = idProd;
            document.getElementById('aiProdName').textContent = 'Per prodotto: ' + nomeProd;
            openModal('modalAddIng');
        }
    </script>
    <datalist id="categorieEsistenti">
        <#if categorieList??>
            <#list categorieList as cat>
                <#if cat?has_content>
                    <option value="${cat}">
                </#if>
            </#list>
        </#if>
    </datalist>
</body>
</html>
