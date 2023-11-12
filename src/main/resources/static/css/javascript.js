const $dropdown = $(".dropdown");
const $dropdownToggle = $(".dropdown-toggle");
const $dropdownMenu = $(".dropdown-menu");
const showClass = "show";
function demo(){
    alert("Hola");
}
    console.log("holaaaa");
function toggleClienteFields() {
        var usarClienteCheckbox = document.getElementById("usar_cliente");
        var clienteSelect = document.getElementById("cliente_select");
        var clienteInput = document.getElementById("cliente_input");

        if (usarClienteCheckbox.checked) {
            clienteSelect.style.display = "none";
            clienteInput.style.display = "block";
        } else {
            clienteSelect.style.display = "block";
            clienteInput.style.display = "none";
        }
    }
$(window).on("load resize", function() {
    console.log("holaaaa");
  if (this.matchMedia("(min-width: 768px)").matches) {
    $dropdown.hover(
      function() {
        const $this = $(this);
        $this.addClass(showClass);
        $this.find($dropdownToggle).attr("aria-expanded", "true");
        $this.find($dropdownMenu).addClass(showClass);
      },
      function() {
        const $this = $(this);
        $this.removeClass(showClass);
        $this.find($dropdownToggle).attr("aria-expanded", "false");
        $this.find($dropdownMenu).removeClass(showClass);
      }
    );
  } else {
    $dropdown.off("mouseenter mouseleave");
  }
});

