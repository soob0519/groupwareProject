$(document).ready(function () {
  $(".main > li > a").click(function (e) {
    e.preventDefault(); // a 태그 기본 동작 막기

    let $submenu = $(this).next(".sub");
    let $arrow = $(this).find(".arr_btm");

    if ($submenu.is(":visible")) {
      // 서브메뉴 닫기 + 화살표 원위치
      $submenu.slideUp(200);
      $arrow.removeClass("rotate");
    } else {
      // 모든 서브메뉴 닫기 + 모든 화살표 원위치
      $(".sub").slideUp(200);
      $(".arr_btm").removeClass("rotate");

      // 현재 메뉴 열기 + 화살표 회전
      $submenu.slideDown(200);
      $arrow.addClass("rotate");
    }
  });
  
  
  // 모달 열기
  $('.date_click').click(function () {
      $('.modal_schedule_write').show();
      $('#modal_1').css({
          top: '80px',
          left: '50%',
          transform: 'translateX(-50%)'
      });
  });
  $('#testbtn').click(function () {
      $('.modal_sd_detail').show();
      $('#modal_2').css({
          top: '80px',
          left: '50%',
          transform: 'translateX(-50%)'
      });
  });


  // 모달 닫기
  $('.close_modal').click(function () {
      $('.modal_schedule_write, .modal_sd_detail').hide();
  });

  // 드래그 설정 (transform 제거)
  $('#modal_1, #modal_2').draggable({
      handle: '.modal_header',
      start: function () {
          $(this).css('transform', 'none');
      }
  });
});