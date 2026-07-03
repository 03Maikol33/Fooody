<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Gestione Caratteristiche e Gruppi – Fooody Owner.">
    <title>Varianti e Gruppi – Fooody Owner</title>
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
            <a href="${context}/owner-menu" class="adminNavLink">
                <span class="material-symbols-outlined">menu_book</span> Gestione Menu
            </a>
            <a href="${context}/owner-characteristics" class="adminNavLink active">
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
            <h1 class="adminTopbarTitle">Gestione Varianti e Gruppi</h1>
            <div style="margin-left:auto;margin-right:16px;display:flex;gap:8px;">
                <button type="button" class="btnOutline" onclick="openModal('modalAddGmc')" style="padding:8px 14px;font-size:13px;">
                    <span class="material-symbols-outlined">library_add</span>Gruppo Mutua Esclusione
                </button>
                <button type="button" class="btnRed" onclick="openModal('modalAddCaratt')" style="padding:8px 14px;font-size:13px;">
                    <span class="material-symbols-outlined">add_circle</span>Caratteristica
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

        <div style="padding:16px;display:flex;flex-direction:column;gap:24px;">

            <!-- GRUPPI MUTUA ESCLUSIONE -->
            <div class="formCard" style="margin:0;">
                <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:12px;display:flex;align-items:center;gap:8px;">
                    <span class="material-symbols-outlined" style="color:#ca0000;">folder_special</span>
                    Gruppi di Mutua Esclusione (GMC)
                </h2>
                <p style="color:#666;font-size:13px;margin-bottom:16px;">
                    I gruppi di mutua esclusione vincolano il cliente a selezionare una singola opzione.
                </p>
                <#if gruppi?has_content>
                    <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(280px, 1fr));gap:14px;">
                        <#list gruppi as g>
                            <div style="border:1px solid #eee;border-radius:8px;padding:12px;background:#fafafa;display:flex;flex-direction:column;justify-content:space-between;">
                                <div>
                                    <div style="font-weight:bold;font-size:16px;color:#222;margin-bottom:4px;">
                                        #${g.idGmc} – ${g.nome}
                                    </div>
                                    <div style="font-size:13px;color:#555;min-height:36px;">
                                        ${g.descrizione!"Nessuna descrizione."}
                                    </div>
                                </div>
                                <div style="display:flex;gap:8px;margin-top:12px;border-top:1px solid #ddd;padding-top:8px;">
                                    <button type="button" class="btnOutline" style="padding:4px 10px;font-size:12px;flex:1;justify-content:center;" onclick="openEditGmc('${g.idGmc}', '${g.nome?js_string}', '${(g.descrizione!"")?js_string}')">Modifica</button>
                                    <form method="post" action="${context}/owner-characteristics" style="margin:0;" onsubmit="return confirm('Vuoi davvero eliminare questo gruppo?');">
                                        <input type="hidden" name="action" value="deleteGmc">
                                        <input type="hidden" name="idGmc" value="${g.idGmc}">
                                        <button type="submit" class="btnGray" style="padding:4px 10px;font-size:12px;color:#ca0000;">Elimina</button>
                                    </form>
                                </div>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <p style="color:#888;font-style:italic;">Nessun gruppo presente.</p>
                </#if>
            </div>

            <!-- CARATTERISTICHE -->
            <div class="formCard" style="margin:0;">
                <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:12px;display:flex;align-items:center;gap:8px;">
                    <span class="material-symbols-outlined" style="color:#ca0000;">checklist</span>
                    Caratteristiche / Varianti Disponibili
                </h2>
                <p style="color:#666;font-size:13px;margin-bottom:16px;">
                    Opzioni aggiuntive associabili ai piatti. Se si specifica un ID Gruppo GMC, saranno opzioni a scelta singola di quel gruppo.
                </p>
                <#if caratteristiche?has_content>
                    <div style="overflow-x:auto;">
                        <table style="width:100%;border-collapse:collapse;font-size:14px;">
                            <thead>
                                <tr style="background:#eee;text-align:left;">
                                    <th style="padding:10px;border-bottom:2px solid #ccc;">ID</th>
                                    <th style="padding:10px;border-bottom:2px solid #ccc;">Nome</th>
                                    <th style="padding:10px;border-bottom:2px solid #ccc;">Prezzo (+€)</th>
                                    <th style="padding:10px;border-bottom:2px solid #ccc;">Default?</th>
                                    <th style="padding:10px;border-bottom:2px solid #ccc;">ID GMC</th>
                                    <th style="padding:10px;border-bottom:2px solid #ccc;text-align:right;">Azioni</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#list caratteristiche as c>
                                    <tr style="border-bottom:1px solid #eee;">
                                        <td style="padding:10px;font-weight:bold;">#${c.idCaratteristica}</td>
                                        <td style="padding:10px;">${c.nome}</td>
                                        <td style="padding:10px;color:${(c.differenzaPrezzo gt 0)?string('#ca0000','#333')};font-weight:bold;">
                                            ${(c.differenzaPrezzo > 0)?string('+','')}${c.differenzaPrezzo?c} €
                                        </td>
                                        <td style="padding:10px;">
                                            <#if c.isDefault>
                                                <span style="background:#e8f5e9;color:#2e7d32;padding:2px 8px;border-radius:12px;font-size:12px;font-weight:bold;">Sì</span>
                                            <#else>
                                                <span style="color:#888;">No</span>
                                            </#if>
                                        </td>
                                        <td style="padding:10px;">
                                            <#if c.idGmc?? && c.idGmc gt 0>
                                                <span style="background:#fff3e0;color:#e65100;padding:2px 8px;border-radius:12px;font-size:12px;font-weight:bold;">GMC #${c.idGmc}</span>
                                            <#else>
                                                <span style="color:#aaa;">Libera (Checkbox)</span>
                                            </#if>
                                        </td>
                                        <td style="padding:10px;text-align:right;display:flex;gap:6px;justify-content:flex-end;">
                                            <button type="button" class="btnOutline" style="padding:4px 8px;font-size:12px;" onclick="openEditCaratt('${c.idCaratteristica}', '${c.nome?js_string}', '${c.differenzaPrezzo?c}', '${c.isDefault?c}', '${(c.idGmc!"")}')">Modifica</button>
                                            <form method="post" action="${context}/owner-characteristics" style="margin:0;" onsubmit="return confirm('Eliminare questa caratteristica?');">
                                                <input type="hidden" name="action" value="deleteCaratteristica">
                                                <input type="hidden" name="idCaratteristica" value="${c.idCaratteristica}">
                                                <button type="submit" class="btnGray" style="padding:4px 8px;font-size:12px;color:#ca0000;">Elimina</button>
                                            </form>
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                <#else>
                    <p style="color:#888;font-style:italic;">Nessuna caratteristica presente.</p>
                </#if>
            </div>

        </div>
    </div>

    <!-- MODAL NUOVO GMC -->
    <div id="modalAddGmc" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:450px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalAddGmc')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Nuovo Gruppo GMC</h2>
            <form method="post" action="${context}/owner-characteristics">
                <input type="hidden" name="action" value="addGmc">
                <div class="formGroup">
                    <label>Nome Gruppo *</label>
                    <input type="text" name="nome" required placeholder="Es. Cottura / Zucchero / Dimensione">
                </div>
                <div class="formGroup">
                    <label>Descrizione</label>
                    <input type="text" name="descrizione" placeholder="Es. Seleziona il livello desiderato">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Crea Gruppo</button>
            </form>
        </div>
    </div>

    <!-- MODAL MODIFICA GMC -->
    <div id="modalEditGmc" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:450px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalEditGmc')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Modifica Gruppo GMC</h2>
            <form method="post" action="${context}/owner-characteristics">
                <input type="hidden" name="action" value="editGmc">
                <input type="hidden" name="idGmc" id="egId">
                <div class="formGroup">
                    <label>Nome Gruppo *</label>
                    <input type="text" name="nome" id="egNome" required>
                </div>
                <div class="formGroup">
                    <label>Descrizione</label>
                    <input type="text" name="descrizione" id="egDesc">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Aggiorna Gruppo</button>
            </form>
        </div>
    </div>

    <!-- MODAL NUOVA CARATTERISTICA -->
    <div id="modalAddCaratt" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:450px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalAddCaratt')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Nuova Caratteristica</h2>
            <form method="post" action="${context}/owner-characteristics">
                <input type="hidden" name="action" value="addCaratteristica">
                <div class="formGroup">
                    <label>Nome Caratteristica *</label>
                    <input type="text" name="nome" required placeholder="Es. Ben Cotto / Senza Zucchero">
                </div>
                <div class="formRow">
                    <div class="formGroup">
                        <label>Variazione Prezzo (€) *</label>
                        <input type="number" step="0.01" name="differenzaPrezzo" required value="0.00">
                    </div>
                    <div class="formGroup">
                        <label>ID Gruppo GMC (opzionale)</label>
                        <input type="number" name="idGmc" placeholder="Es. 1">
                    </div>
                </div>
                <div class="formGroup">
                    <label style="display:flex;align-items:center;gap:8px;cursor:pointer;">
                        <input type="checkbox" name="isDefault" value="true"> È selezionata di default?
                    </label>
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Crea Caratteristica</button>
            </form>
        </div>
    </div>

    <!-- MODAL MODIFICA CARATTERISTICA -->
    <div id="modalEditCaratt" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:450px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalEditCaratt')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Modifica Caratteristica</h2>
            <form method="post" action="${context}/owner-characteristics">
                <input type="hidden" name="action" value="editCaratteristica">
                <input type="hidden" name="idCaratteristica" id="ecId">
                <div class="formGroup">
                    <label>Nome Caratteristica *</label>
                    <input type="text" name="nome" id="ecNome" required>
                </div>
                <div class="formRow">
                    <div class="formGroup">
                        <label>Variazione Prezzo (€) *</label>
                        <input type="number" step="0.01" name="differenzaPrezzo" id="ecPrezzo" required>
                    </div>
                    <div class="formGroup">
                        <label>ID Gruppo GMC</label>
                        <input type="number" name="idGmc" id="ecGmc">
                    </div>
                </div>
                <div class="formGroup">
                    <label style="display:flex;align-items:center;gap:8px;cursor:pointer;">
                        <input type="checkbox" name="isDefault" id="ecDefault" value="true"> È selezionata di default?
                    </label>
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Aggiorna Caratteristica</button>
            </form>
        </div>
    </div>

    <script>
        function openModal(id) {
            document.getElementById(id).style.display = 'flex';
        }
        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }
        function openEditGmc(id, nome, desc) {
            document.getElementById('egId').value = id;
            document.getElementById('egNome').value = nome;
            document.getElementById('egDesc').value = desc;
            openModal('modalEditGmc');
        }
        function openEditCaratt(id, nome, prezzo, isDef, idGmc) {
            document.getElementById('ecId').value = id;
            document.getElementById('ecNome').value = nome;
            document.getElementById('ecPrezzo').value = prezzo;
            document.getElementById('ecDefault').checked = (isDef === 'true');
            document.getElementById('ecGmc').value = (idGmc && idGmc !== '0') ? idGmc : '';
            openModal('modalEditCaratt');
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
