<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Accedi o registrati a Fooody per ordinare a domicilio facilmente.">
    <title>Accedi – Fooody Ristorante e Delivery</title>
    <link rel="stylesheet" href="${context}/style.css">
    <link rel="stylesheet" href="${context}/fonts.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
</head>
<body>
    <div class="headerBar">
        <div class="logo">
            <p class="logoText"><a href="${context}/index" style="text-decoration:none;color:inherit;">Fooody</a></p>
            <p class="logoDescriptionText">Ristorante e delivery</p>
        </div>
        <div class="navigationBar">
            <div><a href="${context}/index">Home</a></div>
            <div><a href="${context}/menu">Menu</a></div> </div>
    </div>

    <div class="authPage">
        <div class="authCard">
            <div class="authLogoArea">
                <p class="authLogoText">Fooody</p>
                <p class="authLogoSub">Ordina a domicilio in pochi click</p>
            </div>

            <div class="authTabs">
                <button class="authTab <#if !regError??>active</#if>" id="tabLogin" onclick="switchTab('login')">
                    <span class="material-symbols-outlined" style="font-size:16px;vertical-align:middle;">login</span>
                    Accedi
                </button>
                <button class="authTab <#if regError??>active</#if>" id="tabReg" onclick="switchTab('register')">
                    <span class="material-symbols-outlined" style="font-size:16px;vertical-align:middle;">person_add</span>
                    Registrati
                </button>
            </div>

            <form id="loginForm" action="${context}/login" method="POST" novalidate <#if regError??>style="display:none;"</#if>>
                
                <#if loginError??>
                    <div class="errorMsg" style="display:block;">${loginError}</div>
                </#if>

                <div class="formGroup">
                    <label for="loginEmail">Email</label>
                    <input type="email" id="loginEmail" name="email" value="${emailInserita!''}" placeholder="la-tua@email.it" required autocomplete="email">
                </div>
                <div class="formGroup">
                    <label for="loginPassword">Password</label>
                    <input type="password" id="loginPassword" name="password" placeholder="••••••••" required autocomplete="current-password">
                </div>
                <button type="submit" class="btnRed" id="loginSubmit" style="width:100%;justify-content:center;margin-top:8px;">
                    <span class="material-symbols-outlined">login</span> Accedi
                </button>
                <div class="authHint">
                    <strong>Profili di demo:</strong> cliente: maikol@cliente.it / hashedpassword3 &nbsp;|&nbsp;
                    staff: staff@webdelivery.it / hashedpassword2 &nbsp;|&nbsp;
                    owner: admin@webdelivery.it / hashedpassword1
                </div>
            </form>

            <form id="registerForm" action="${context}/register" method="POST" novalidate <#if !regError??>style="display:none;"</#if>>
                
                <#if regError??>
                    <div class="errorMsg" style="display:block;">${regError}</div>
                </#if>

                <div class="formRow">
                    <div class="formGroup">
                        <label for="regFirstName">Nome *</label>
                        <input type="text" id="regFirstName" name="nome" placeholder="Mario" required>
                    </div>
                    <div class="formGroup">
                        <label for="regLastName">Cognome *</label>
                        <input type="text" id="regLastName" name="cognome" placeholder="Rossi" required>
                    </div>
                </div>
                <div class="formGroup">
                    <label for="regEmail">Email *</label>
                    <input type="email" id="regEmail" name="email" placeholder="la-tua@email.it" required autocomplete="email">
                </div>
                <div class="formGroup">
                    <label for="regPhone">Telefono * <small>(necessario per la consegna)</small></label>
                    <input type="tel" id="regPhone" name="telefono" placeholder="+39 333 1234567" required>
                </div>
                <div class="formRow">
                    <div class="formGroup" style="flex: 2;">
                        <label for="regVia">Via / Piazza *</label>
                        <input type="text" id="regVia" name="via" placeholder="Via Roma" required>
                    </div>
                    <div class="formGroup" style="flex: 1;">
                        <label for="regCivico">N. Civico *</label>
                        <input type="text" id="regCivico" name="civico" placeholder="1" required>
                    </div>
                </div>
                <div class="formGroup">
                    <label for="regCitta">Città *</label>
                    <input type="text" id="regCitta" name="citta" placeholder="Alba Adriatica" required>
                </div>
                <div class="formGroup">
                    <label for="regPassword">Password * <small>(min. 6 caratteri)</small></label>
                    <input type="password" id="regPassword" name="password" placeholder="••••••••" required minlength="6" autocomplete="new-password">
                </div>
                <button type="submit" class="btnRed" id="regSubmit" style="width:100%;justify-content:center;margin-top:8px;">
                    <span class="material-symbols-outlined">person_add</span> Crea Account
                </button>
            </form>
        </div>
    </div>

    <script>
        
        function switchTab(tab) {
            document.getElementById('loginForm').style.display = tab === 'login' ? '' : 'none';
            document.getElementById('registerForm').style.display = tab === 'register' ? '' : 'none';
            
            if (tab === 'login') {
                document.getElementById('tabLogin').classList.add('active');
                document.getElementById('tabReg').classList.remove('active');
            } else {
                document.getElementById('tabReg').classList.add('active');
                document.getElementById('tabLogin').classList.remove('active');
            }
        }
    </script>
</body>
</html>