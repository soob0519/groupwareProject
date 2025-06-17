$(function () {
  $(".main > li > a").click(function (e) {
    e.preventDefault(); // a 태그 기본 동작 막기

    let $submenu = $(this).next(".sub");

    // 이미 열려 있는 경우 닫기
    if ($submenu.is(":visible")) {
      $submenu.slideUp(200);
    } else {
      // 다른 메뉴 닫기
      $(".sub").slideUp(200);
      // 현재 클릭한 메뉴 열기
      $submenu.slideDown(200);
    }
  });
});
