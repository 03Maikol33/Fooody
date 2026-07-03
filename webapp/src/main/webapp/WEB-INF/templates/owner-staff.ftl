<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Gestione Staff – Fooody Owner: gestisci il personale del ristorante.">
    <title>Gestione Staff – Fooody Owner</title>
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
            <a href="${context}/owner-characteristics" class="adminNavLink">
                <span class="material-symbols-outlined">tune</span> Varianti e Gruppi
            </a>
            <a href="${context}/owner-ingredients" class="adminNavLink">
                <span class="material-symbols-outlined">kitchen</span> Gestione Ingredienti
            </a>
            <a href="${context}/owner-staff" class="adminNavLink active">
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
            <h1 class="adminTopbarTitle">Gestione Staff</h1>
            <div style="margin-left:auto;margin-right:16px;display:flex;gap:8px;">
                <button type="button" class="btnOutline" onclick="openModal('modalPromote')" style="padding:8px 14px;font-size:13px;">
                    <span class="material-symbols-outlined">upgrade</span> Promuovi Cliente
                </button>
                <button type="button" class="btnRed" onclick="openModal('modalHire')" style="padding:8px 14px;font-size:13px;">
                    <span class="material-symbols-outlined">person_add</span> Nuovo Staff
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

        <!-- GRIGLIA STAFF -->
        <div style="padding:16px;">
            <#if personaleList?has_content>
                <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(280px, 1fr));gap:16px;">
                    <#list personaleList as p>
                        <div class="formCard" style="display:flex;flex-direction:column;justify-content:space-between;margin:0;">
                            <div>
                                <div style="display:flex;align-items:center;gap:12px;margin-bottom:12px;">
                                    <div style="width:48px;height:48px;border-radius:50%;background:#ca0000;color:white;display:flex;align-items:center;justify-content:center;font-size:20px;font-weight:bold;font-family:'Limelight',sans-serif;">
                                        ${p.nome?substring(0,1)}
                                    </div>
                                    <div>
                                        <h3 style="font-family:'Limelight',sans-serif;font-size:18px;margin:0;">
                                            ${p.nome} ${p.cognome}
                                        </h3>
                                        <span class="statusBadge" style="background:#2d6a4f;font-size:10px;margin-top:4px;">Personale</span>
                                    </div>
                                </div>
                                <div style="font-family:'Courier New',monospace;font-size:13px;color:#555;margin-bottom:16px;">
                                    <p style="margin:4px 0;"><strong>ID Staff:</strong> #${p.idPersonale}</p>
                                    <p style="margin:4px 0;"><strong>ID Utente:</strong> #${p.idUtente}</p>
                                </div>
                            </div>
                            <div style="border-top:1px solid #eee;padding-top:12px;text-align:right;">
                                <form method="post" action="${context}/owner-staff" style="margin:0;">
                                    <input type="hidden" name="action" value="fire">
                                    <input type="hidden" name="idPersonale" value="${p.idPersonale}">
                                    <button type="submit" class="btnOutline" style="color:#ca0000;border-color:#ca0000;padding:6px 12px;font-size:12px;"
                                            onclick="return confirm('Rimuovere ${p.nome} ${p.cognome} dallo staff?')">
                                        <span class="material-symbols-outlined" style="font-size:16px;">person_remove</span> Rimuovi
                                    </button>
                                </form>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="emptyState">
                    <span class="material-symbols-outlined">badge</span>
                    <p>Nessun membro del personale presente.</p>
                </div>
            </#if>
        </div>
    </div>

    <!-- MODAL NUOVO STAFF -->
    <div id="modalHire" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:450px;width:90%;max-height:90vh;overflow-y:auto;position:relative;">
            <button type="button" onclick="closeModal('modalHire')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Registra Nuovo Staff</h2>
            <form method="post" action="${context}/owner-staff">
                <input type="hidden" name="action" value="hire">
                <div class="formRow">
                    <div class="formGroup">
                        <label>Nome *</label>
                        <input type="text" name="nome" required placeholder="Es. Luca">
                    </div>
                    <div class="formGroup">
                        <label>Cognome *</label>
                        <input type="text" name="cognome" required placeholder="Es. Bianchi">
                    </div>
                </div>
                <div class="formGroup">
                    <label>Email *</label>
                    <input type="email" name="email" required placeholder="luca.bianchi@fooody.it">
                </div>
                <div class="formRow">
                    <div class="formGroup">
                        <label>Password *</label>
                        <input type="password" name="password" required placeholder="Minimo 6 caratteri">
                    </div>
                    <div class="formGroup">
                        <label>Telefono</label>
                        <input type="tel" name="telefono" placeholder="+39 ...">
                    </div>
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Crea Account Staff</button>
            </form>
        </div>
    </div>

    <!-- MODAL PROMUOVI CLIENTE -->
    <div id="modalPromote" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:400px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalPromote')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Promuovi Cliente</h2>
            <p style="font-family:'Courier New',monospace;font-size:13px;color:#666;margin-bottom:16px;">
                Inserisci l'ID Utente di un account cliente esistente per promuoverlo al ruolo di Personale.
            </p>
            <form method="post" action="${context}/owner-staff">
                <input type="hidden" name="action" value="promote">
                <div class="formGroup">
                    <label>ID Utente *</label>
                    <input type="number" name="idUtente" required placeholder="Es. 15">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Promuovi a Staff</button>
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
    </script>
</body>
</html>
