<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Sfoglia il menu di Fooody – Pizza, Hamburger, Pasta, Dessert e molto altro. Ordina a domicilio!">
    <title>Menu – Fooody Ristorante e Delivery</title>
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
            <div><a href="${context}/menu" style="color:#ca0000;font-weight:bold;">Menu</a></div>
            <div><a href="${context}/index#contatti">Contatti</a></div>
            <#if utenteLoggato?? && utenteLoggato.ruolo == "cliente">
                <div><a href="${context}/orders">I miei ordini</a></div>
            <#elseif utenteLoggato?? && utenteLoggato.ruolo == "proprietario">
                <div><a href="${context}/owner-orders" style="color:#ca0000;font-weight:bold;">Area Proprietario</a></div>
            <#elseif utenteLoggato?? && (utenteLoggato.ruolo == "personale" || utenteLoggato.ruolo == "staff")>
                <div><a href="${context}/staff-orders" style="color:#ca0000;font-weight:bold;">Area Staff</a></div>
            </#if>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
            <#if utenteLoggato??>
                <#if utenteLoggato.ruolo == "cliente">
                    <a href="${context}/cart" id="navCartLink" class="navCartLink nav-extra">
                        <span class="material-symbols-outlined">shopping_cart</span>
                        <#if (cartCount?? && cartCount > 0)>
                            <span id="cartBadgeCount" style="background:#ca0000;color:white;border-radius:50%;padding:2px 6px;font-size:11px;font-weight:bold;">
                                ${cartCount}
                            </span>
                        </#if>
                    </a>
                </#if>
                <a href="${context}/profile" class="accediBtn" style="text-decoration:none;">
                    <span class="material-symbols-outlined">person</span>
                    ${utenteLoggato.nome}
                </a>
            <#else>
                <a href="${context}/login" class="accediBtn" style="text-decoration:none;">
                    <span class="material-symbols-outlined">login</span>
                    Accedi
                </a>
            </#if>
        </div>
        <span id="menuBtn" class="material-symbols-outlined menuIcon">menu</span>
    </div>

    <!-- Menu mobile -->
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

    <!-- PAGE HEADER -->
    <div class="menuPageHeader">
        <h1 class="doubleColorTitle">
            <div>Il nostro</div>
            <div>Menu</div>
        </h1>
        <p class="subtitle">Prodotti freschi preparati con cura. Filtra per categoria o cerca il tuo piatto preferito.</p>
    </div>

    <!-- BARRA FILTRI (form GET per server-side filtering) -->
    <div class="filterBar">
        <a href="${context}/menu" class="filterChip<#if filtroCategoria == ''> active</#if>">Tutti</a>
        <#if categorieList??>
            <#list categorieList as cat>
                <#if cat?has_content>
                    <a href="${context}/menu?categoria=${cat?url('UTF-8')}"
                       class="filterChip<#if filtroCategoria == cat> active</#if>">
                        ${cat}
                    </a>
                </#if>
            </#list>
        </#if>
    </div>

    <!-- RICERCA (form GET) -->
    <form method="get" action="${context}/menu" class="menuSearchBar" style="margin:0;">
        <input type="text" name="nome" id="menuSearch"
               placeholder="Cerca nel menu..."
               value="${filtroNome}">
        <#if filtroCategoria != "">
            <input type="hidden" name="categoria" value="${filtroCategoria}">
        </#if>
    </form>

    <!-- GRIGLIA PRODOTTI -->
    <#if errorMsg??>
        <div class="emptyState">
            <span class="material-symbols-outlined" style="font-size:48px;color:#ddd;">wifi_off</span>
            <p>${errorMsg}</p>
        </div>
    <#elseif prodotti?has_content>
        <div class="menuGrid" id="menuGrid">
            <#list prodotti as prodotto>
                <div class="menuCard" id="prodotto-${prodotto.idProdotto}">
                    <img src="${context}/img/2.jpg" alt="${prodotto.nome}" loading="lazy" onclick="openAddToCartModal(${prodotto.idProdotto})" style="cursor:pointer;">
                    <div class="menuCardBody" onclick="openAddToCartModal(${prodotto.idProdotto})" style="cursor:pointer;">
                        <span class="menuCardCategory">${prodotto.categoria!"–"}</span>
                        <h3 class="menuCardTitle">${prodotto.nome}</h3>
                        <p class="menuCardDesc">
                            ${(prodotto.descrizione?length > 70)?then(
                                prodotto.descrizione?substring(0,70) + '…',
                                prodotto.descrizione!""
                            )}
                        </p>
                        <#if prodotto.ingredienti?has_content>
                            <p style="font-size:11px;color:#d32f2f;margin:6px 0 0;line-height:1.3;font-family:'Courier New',monospace;">
                                 <#list prodotto.ingredienti as ing>${ing.nome}<#if ing.quantita??> (${ing.quantita})</#if><#if ing?has_next>, </#if></#list>
                            </p>
                        </#if>
                        <div class="menuCardFooter" onclick="event.stopPropagation();">
                            <span class="menuCardPrice">€ ${prodotto.prezzoBase?string["0.00"]}</span>
                            <button type="button" class="menuCardAddBtn"
                                    onclick="openAddToCartModal(${prodotto.idProdotto})"
                                    title="Vedi opzioni e aggiungi">+</button>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div id="noResults" class="emptyState">
            <span class="material-symbols-outlined" style="font-size:48px;color:#ddd;">search_off</span>
            <p>Nessun prodotto trovato.</p>
            <a href="${context}/menu" class="btnRed">Azzera ricerca</a>
        </div>
    </#if>

    <#-- CARRELLO FLOTTANTE (solo clienti) -->
    <#if utenteLoggato?? && utenteLoggato.ruolo == "cliente">
        <a href="${context}/cart" class="floatingCart" id="floatingCart">
            <span class="material-symbols-outlined">shopping_cart</span>
            <span>Carrello</span>
            <#if (cartCount?? && cartCount > 0)>
                <span class="floatingCartCount">${cartCount}</span>
            </#if>
        </a>
    </#if>

    <!-- MODAL AGGIUNGI AL CARRELLO / DETTAGLI -->
    <div id="productModal" class="modalOverlay" style="display:none;" onclick="if(event.target===this)closeModal()">
        <div class="modalBox" style="max-width:580px;">
            <button class="modalClose" onclick="closeModal()">
                <span class="material-symbols-outlined">close</span>
            </button>
            <img id="pmImg" src="${context}/img/2.jpg" alt="" style="width:100%;height:220px;object-fit:cover;border-radius:10px;margin-bottom:16px;">
            <span id="pmCat" class="menuCardCategory"></span>
            <h2 id="pmTitle" style="font-family:'Limelight',sans-serif;font-size:24px;margin:8px 0;"></h2>
            <p id="pmDesc" style="font-family:'Courier New',monospace;font-size:14px;color:#555;margin-bottom:14px;"></p>
            <div id="pmIngSection" style="display:none;margin-bottom:14px;background:#fff8e1;border:1px solid #ffe0b2;padding:8px 12px;border-radius:6px;">
                <p style="font-family:'Courier New',monospace;font-size:12px;font-weight:bold;color:#e65100;margin:0 0 4px;">INGREDIENTI:</p>
                <div id="pmIngList" style="font-family:'Courier New',monospace;font-size:13px;color:#333;"></div>
            </div>

            <!-- Form POST verso /cart?action=add -->
            <form id="addToCartForm" method="post" action="${context}/cart" onsubmit="return prepareFeaturesSubmit()">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="idProdotto" id="fmIdProdotto">
                <input type="hidden" name="nomeProdotto" id="fmNomeProdotto">
                <input type="hidden" name="prezzoBase" id="fmPrezzoBase">
                <input type="hidden" name="tempoPreparazione" id="fmTempoPreparazione" value="15">
                <input type="hidden" name="immagine" id="fmImmagine" value="">

                <!-- Caratteristiche (popolate dinamicamente da JS) -->
                <div id="pmFeaturesSection" style="display:none;">
                    <p style="font-family:'Courier New',monospace;font-size:13px;font-weight:bold;color:#333;margin-bottom:8px;">PERSONALIZZA:</p>
                    <div id="pmFeatures"></div>
                </div>

                <!-- Prezzo + Quantità -->
                <div style="display:flex;justify-content:space-between;align-items:center;margin-top:16px;flex-wrap:wrap;gap:12px;">
                    <div>
                        <p id="pmPrice" style="font-family:'Courier New',monospace;font-size:24px;color:#ca0000;font-weight:bold;margin:0;"></p>
                        <p id="pmTime" style="font-family:'Courier New',monospace;font-size:13px;color:#888;margin:4px 0 0;"></p>
                    </div>
                    <div class="qtySelector">
                        <button type="button" class="qtyBtn" id="pmMinus">−</button>
                        <input type="hidden" name="quantita" id="fmQuantita" value="1">
                        <span class="qtyNum" id="pmQnum">1</span>
                        <button type="button" class="qtyBtn" id="pmPlus">+</button>
                    </div>
                </div>

                <#if utenteLoggato?? && utenteLoggato.ruolo == "cliente">
                    <button type="submit" class="btnRed" id="pmAddCart" style="width:100%;justify-content:center;margin-top:14px;">
                        <span class="material-symbols-outlined">add_shopping_cart</span> Aggiungi al carrello
                    </button>
                <#else>
                    <a href="${context}/login" class="btnRed" style="width:100%;justify-content:center;margin-top:14px;text-decoration:none;display:flex;">
                        <span class="material-symbols-outlined">login</span> Accedi come Cliente per Ordinare
                    </a>
                </#if>
            </form>
        </div>
    </div>

    <!-- FOOTER -->
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
        <p>&copy; 2026 Fooody. Tutti i diritti riservati.</p>
    </footer>

    <script>
        // Dati prodotti iniettati dal server (FreeMarker → JSON)
        var PRODOTTI_JSON = [
            <#list prodotti as p>
            {
                "id": ${p.idProdotto},
                "nome": "${p.nome?js_string}",
                "categoria": "${(p.categoria!"")?js_string}",
                "descrizione": "${(p.descrizione!"")?js_string}",
                "prezzoBase": ${p.prezzoBase?c},
                "tempoPreparazione": ${p.tempoPreparazione!0},
                "ingredienti": [
                    <#if p.ingredienti?has_content>
                        <#list p.ingredienti as ing>
                        {
                            "idIngrediente": ${ing.idIngrediente},
                            "nome": "${ing.nome?js_string}",
                            "quantita": <#if ing.quantita??>"${ing.quantita?js_string}"<#else>null</#if>
                        }<#if ing?has_next>,</#if>
                        </#list>
                    </#if>
                ],
                "caratteristiche": [
                    <#if p.caratteristiche?has_content>
                        <#list p.caratteristiche as c>
                        {
                            "idCaratteristica": ${c.idCaratteristica},
                            "nome": "${c.nome?js_string}",
                            "differenzaPrezzo": ${c.differenzaPrezzo?c},
                            "isDefault": ${c.isDefault?c},
                            "idGmc": <#if c.idGmc?? && c.idGmc &gt; 0>${c.idGmc?c}<#else>null</#if>,
                            "gruppo": <#if c.gruppo??>"${c.gruppo?js_string}"<#else>null</#if>
                        }<#if c?has_next>,</#if>
                        </#list>
                    </#if>
                ]
            }<#if p?has_next>,</#if>
            </#list>
        ];

        // ---- Ricerca live (lato client, filtra la griglia già renderizzata) ----
        var searchInput = document.getElementById('menuSearch');
        if (searchInput) {
            // La ricerca submit avviene onEnter (form GET) → server filtra
            // ma mostriamo anche un filtro client-side immediato
            searchInput.addEventListener('input', function() {
                var q = this.value.trim().toLowerCase();
                document.querySelectorAll('.menuCard').forEach(function(card) {
                    var title = card.querySelector('.menuCardTitle');
                    var desc = card.querySelector('.menuCardDesc');
                    var match = !q ||
                        (title && title.textContent.toLowerCase().indexOf(q) !== -1) ||
                        (desc && desc.textContent.toLowerCase().indexOf(q) !== -1);
                    card.style.display = match ? '' : 'none';
                });
            });
        }

        // ---- Modal Aggiungi al Carrello ----
        var currentProduct = null;
        var pmQty = 1;

        function openAddToCartModal(productId) {
            var p = PRODOTTI_JSON.find(function(x) { return x.id === productId; });
            if (!p) return;
            currentProduct = p;
            pmQty = 1;

            document.getElementById('pmCat').textContent = p.categoria;
            document.getElementById('pmTitle').textContent = p.nome;
            document.getElementById('pmDesc').textContent = p.descrizione;
            document.getElementById('pmQnum').textContent = 1;
            document.getElementById('pmTime').innerHTML = p.tempoPreparazione > 0 ? '<span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle;">timer</span> Tempo prep.: ' + p.tempoPreparazione + ' min' : '';
            document.getElementById('fmIdProdotto').value = p.id;
            document.getElementById('fmNomeProdotto').value = p.nome;
            document.getElementById('fmPrezzoBase').value = p.prezzoBase;
            document.getElementById('fmTempoPreparazione').value = p.tempoPreparazione || 15;
            document.getElementById('fmQuantita').value = 1;

            // Ingredienti
            var ingSec = document.getElementById('pmIngSection');
            var ingList = document.getElementById('pmIngList');
            if (p.ingredienti && p.ingredienti.length > 0) {
                ingSec.style.display = '';
                ingList.innerHTML = p.ingredienti.map(function(i) {
                    return i.nome + (i.quantita ? ' (' + i.quantita + ')' : '');
                }).join(', ');
            } else {
                ingSec.style.display = 'none';
                ingList.innerHTML = '';
            }

            // Caratteristiche
            var featSec = document.getElementById('pmFeaturesSection');
            var featDiv = document.getElementById('pmFeatures');
            if (p.caratteristiche && p.caratteristiche.length > 0) {
                featSec.style.display = '';
                featDiv.innerHTML = renderFeatures(p);
            } else {
                featSec.style.display = 'none';
                featDiv.innerHTML = '';
            }

            updatePrice();
            document.getElementById('productModal').style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }

        function renderFeatures(p) {
            var html = '';
            
            // Raggruppa usando il vero ID del Database (idGmc)
            var grouped = {};
            var ungrouped = [];
            
            p.caratteristiche.forEach(function(f) {
                if (f.idGmc !== null) {
                    if (!grouped[f.idGmc]) {
                        grouped[f.idGmc] = {
                            nomeGruppo: f.gruppo || 'Scelta Obbligatoria',
                            feats: []
                        };
                    }
                    grouped[f.idGmc].feats.push(f);
                } else {
                    ungrouped.push(f);
                }
            });

            //gruppi di mutua esclusione
            Object.keys(grouped).forEach(function(gmcId) {
                var group = grouped[gmcId];
                html += '<div class="featGroup"><div class="featGroupTitle">' + group.nomeGruppo + ' <small style="font-weight:normal;color:#888;">(scelta singola)</small></div>';
                
                group.feats.forEach(function(f, i) {
                    var label = f.nome + (f.differenzaPrezzo !== 0 ? ' (' + (f.differenzaPrezzo > 0 ? '+' : '') + '€' + f.differenzaPrezzo.toFixed(2) + ')' : '');
                    var checkedAttr = f.isDefault ? ' checked data-was-checked="true"' : ' data-was-checked="false"';
                   
                    html += '<label class="featOption"><input type="radio" name="gmc_' + gmcId + '" class="feat-radio" value="' + f.idCaratteristica + '"' + checkedAttr + ' onclick="onRadioClick(this)"> ' + label + '</label>';
                });
                html += '</div>';
            });

            //opzioni svincolate
            if (ungrouped.length > 0) {
                html += '<div class="featGroup"><div class="featGroupTitle">Opzioni aggiuntive</div>';
                ungrouped.forEach(function(f) {
                    var label = f.nome + (f.differenzaPrezzo !== 0 ? ' (' + (f.differenzaPrezzo > 0 ? '+' : '') + '€' + f.differenzaPrezzo.toFixed(2) + ')' : '');
                    html += '<label class="featOption"><input type="checkbox" class="feat-check" value="' + f.idCaratteristica + '"' + (f.isDefault ? ' checked' : '') + ' onchange="updatePrice()"> ' + label + '</label>';
                });
                html += '</div>';
            }
            return html;
        }

        function onRadioClick(el) {
            if (el.dataset.wasChecked === 'true') {
                el.checked = false;
                el.dataset.wasChecked = 'false';
            } else {
                document.querySelectorAll('input[name="' + el.name + '"]').forEach(function(r) {
                    r.dataset.wasChecked = 'false';
                });
                el.dataset.wasChecked = 'true';
            }
            updatePrice();
        }

        function getSelectedFeaturesPrice() {
            if (!currentProduct) return 0;
            var diff = 0;
            // Radio buttons (gruppi)
            document.querySelectorAll('#pmFeatures input[type="radio"]:checked').forEach(function(r) {
                var f = currentProduct.caratteristiche.find(function(x) { return x.idCaratteristica == r.value; });
                if (f) diff += f.differenzaPrezzo;
            });
            // Checkboxes
            document.querySelectorAll('#pmFeatures input[type="checkbox"]:checked').forEach(function(c) {
                var f = currentProduct.caratteristiche.find(function(x) { return x.idCaratteristica == c.value; });
                if (f) diff += f.differenzaPrezzo;
            });
            return diff;
        }

        function updatePrice() {
            if (!currentProduct) return;
            var diff = getSelectedFeaturesPrice();
            var total = (currentProduct.prezzoBase + diff) * pmQty;
            document.getElementById('pmPrice').textContent = '€ ' + total.toFixed(2);
            document.getElementById('fmPrezzoBase').value = (currentProduct.prezzoBase + diff).toFixed(2);
        }

        function prepareFeaturesSubmit() {
            document.querySelectorAll('.hidden-feat-input').forEach(function(el) { el.remove(); });
            var form = document.getElementById('addToCartForm');
            
            document.querySelectorAll('#pmFeatures input[type="radio"]:checked, #pmFeatures input[type="checkbox"]:checked').forEach(function(input) {
                var f = currentProduct.caratteristiche.find(function(x) { return x.idCaratteristica == input.value; });
                if (f) {
                    var hId = document.createElement('input');
                    hId.type = 'hidden';
                    hId.name = 'caratteristica';
                    hId.value = f.idCaratteristica;
                    hId.className = 'hidden-feat-input';
                    form.appendChild(hId);
                    
                    var hNome = document.createElement('input');
                    hNome.type = 'hidden';
                    hNome.name = 'nomeCaratteristica';
                    hNome.value = f.nome;
                    hNome.className = 'hidden-feat-input';
                    form.appendChild(hNome);
                }
            });
            return true;
        }

        document.getElementById('pmMinus') && document.getElementById('pmMinus').addEventListener('click', function() {
            if (pmQty > 1) { pmQty--; document.getElementById('pmQnum').textContent = pmQty; document.getElementById('fmQuantita').value = pmQty; updatePrice(); }
        });
        document.getElementById('pmPlus') && document.getElementById('pmPlus').addEventListener('click', function() {
            pmQty++; document.getElementById('pmQnum').textContent = pmQty; document.getElementById('fmQuantita').value = pmQty; updatePrice();
        });

        function closeModal() {
            document.getElementById('productModal').style.display = 'none';
            document.body.style.overflow = '';
        }
        document.addEventListener('keydown', function(e) { if (e.key === 'Escape') closeModal(); });
    </script>
    <script src="${context}/scripts/app.js"></script>
</body>
</html>
