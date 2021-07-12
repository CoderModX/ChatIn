/* For about block */
var about = document.getElementById('about_button'); // Assumes element with id='button'

about.onclick = function() {
    var div = document.getElementById('about_text');
    if (div.style.display !== 'none') {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
};

function showa(){
    document.getElementById('about_text').style.display = "block";}
function hidea(){
    document.getElementById('about_text').style.display = "none";}


/* For feature block */

var feature = document.getElementById('feature_button'); // Assumes element with id='button'

feature.onclick = function() {
    var div = document.getElementById('feature_text');
    if (div.style.display !== 'none') {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
};

function showf(){
    document.getElementById('feature_text').style.display = "block";}
function hidef(){
    document.getElementById('feature_text').style.display = "none";}
