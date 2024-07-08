//등록 모달
const examRegBtn = document.querySelector(".exam__btn-register");
const examRegModal = document.querySelector(".exam__modal-register");
const examRegModalOverlay = document.querySelector(".exam__modal__overlay");
examRegBtn.addEventListener("click",handleRegBtnClick);

function handleRegBtnClick(event){
    event.preventDefault();
    examRegModal.classList.remove("hidden")
    examRegModalOverlay.addEventListener("click",()=>{
        examRegModal.classList.add("hidden")
    });
}

const examToggleDelBtn = document.querySelector(".exam__btn-delete");
const examDelBtns = document.querySelectorAll(".exam__item__delete-btn");
examToggleDelBtn.addEventListener("click",handleToggleDelClick);

function handleToggleDelClick(event) {
    event.preventDefault();
    examDelBtns.forEach(btn => {
        btn.classList.toggle("hidden");
    });
}