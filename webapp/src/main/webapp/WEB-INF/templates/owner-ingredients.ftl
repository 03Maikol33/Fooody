<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Gestione Ingredienti – Fooody Owner.">
    <title>Gestione Ingredienti – Fooody Owner</title>
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
            <a href="${context}/owner-ingredients" class="adminNavLink active">
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
            <h1 class="adminTopbarTitle">Gestione Dizionario Ingredienti</h1>
            <div style="margin-left:auto;margin-right:16px;display:flex;gap:8px;">
                <button type="button" class="btnRed" onclick="openModal('modalAddIngrediente')" style="padding:8px 14px;font-size:13px;">
                    <span class="material-symbols-outlined">add_circle</span>Nuovo Ingrediente
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

            <div class="formCard" style="margin:0;">
                <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:12px;display:flex;align-items:center;gap:8px;">
                    <span class="material-symbols-outlined" style="color:#ca0000;">kitchen</span>
                    Dizionario Globale degli Ingredienti
                </h2>
                <p style="color:#666;font-size:13px;margin-bottom:16px;">
                    In questa sezione puoi aggiungere nuovi ingredienti al sistema o eliminare quelli esistenti. Gli ingredienti creati qui potranno poi essere associati ai prodotti nel menu specificando la quantità necessaria.
                </p>
                <#if ingredienti?has_content>
                    <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(260px, 1fr));gap:14px;">
                        <#list ingredienti as ing>
                            <div style="border:1px solid #eee;border-radius:8px;padding:14px;background:#fafafa;display:flex;align-items:center;justify-content:space-between;gap:12px;">
                                <div style="display:flex;align-items:center;gap:10px;">
                                    <span class="material-symbols-outlined" style="color:#ff8c00;font-size:24px;">eco</span>
                                    <div>
                                        <div style="font-weight:bold;font-size:15px;color:#222;">
                                            ${ing.nome}
                                        </div>
                                        <div style="font-size:11px;color:#888;font-family:'Courier New',monospace;">
                                            ID: #${ing.idIngrediente}
                                        </div>
                                    </div>
                                </div>
                                <form method="post" action="${context}/owner-ingredients" style="margin:0;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="idIngrediente" value="${ing.idIngrediente}">
                                    <button type="submit" class="btnOutline" title="Elimina Ingrediente"
                                            onclick="return confirm('Sei sicuro di voler eliminare l\'ingrediente \'${ing.nome?js_string}\'?')"
                                            style="padding:6px;border-color:#ffcdd2;color:#d32f2f;display:flex;align-items:center;">
                                        <span class="material-symbols-outlined" style="font-size:18px;">delete</span>
                                    </button>
                                </form>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <p style="color:#888;font-style:italic;">Nessun ingrediente presente nel dizionario.</p>
                </#if>
            </div>

        </div>
    </div>

    <!-- MODAL CREA INGREDIENTE -->
    <div id="modalAddIngrediente" class="productModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.6);z-index:200;align-items:center;justify-content:center;">
        <div class="formCard" style="max-width:440px;width:90%;position:relative;">
            <button type="button" onclick="closeModal('modalAddIngrediente')" style="position:absolute;top:12px;right:12px;background:none;border:none;cursor:pointer;color:#555;"><span class="material-symbols-outlined">close</span></button>
            <h2 style="font-family:'Limelight',sans-serif;font-size:20px;margin-bottom:16px;">Nuovo Ingrediente</h2>
            <form method="post" action="${context}/owner-ingredients">
                <input type="hidden" name="action" value="create">
                <div class="formGroup">
                    <label>Nome dell'ingrediente *</label>
                    <input type="text" name="nome" required placeholder="Es. Pomodoro San Marzano, Mozzarella di Bufala">
                </div>
                <button type="submit" class="btnRed" style="width:100%;justify-content:center;margin-top:10px;">Crea Ingrediente</button>
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

        function openModal(id) {
            document.getElementById(id).style.display = 'flex';
        }
        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }
    </script>
</body>
</html>
