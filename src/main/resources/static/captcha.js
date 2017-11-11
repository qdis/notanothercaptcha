(function() {

    var captchas = document.getElementsByClassName("noc");

    for (var i = 0; i < captchas.length; i++) {
        var captcha = captchas[i];

        var clientId = captcha.getAttribute("data-noc-client-id");

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {

                var resp = JSON.parse(this.responseText);
                captcha.innerHTML= ""+
                    "<h3>To prove you are a machine, read this QR Code</h3>"+
                    "<img src=\""+resp.data+"\" style='margin: 0 auto; display: block;'/>"+
                    "<label>Response:</label>"+
                    "<input type=\"text\" name=\"nocResponse\" style='width: 100%'>"+
                    "<input type=\"hidden\" name=\"nocId\" value=\""+resp.captchaId+"\" ><br>";
                captcha.style.padding="16px";
                captcha.style.maxWidth="300px";
                captcha.style.border="3px solid #ff9800";
                captcha.style.color="#29b6f6";
                captcha.style.fontFamily="\"Comic Sans MS\", cursive, sans-serif";


            }else{
                captcha.innerHTML= ""+
                    "<h3>Captcha not available</h3>";
            }
        };
        xhttp.open("GET", "https://notanothercaptcha.com/captcha/generate/"+clientId, true);
        xhttp.send();

    }
    // your page initialization code here
    // the DOM will be available here

})();