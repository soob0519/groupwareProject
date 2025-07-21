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
  
  
  // 일정 등록 모달 열기
    $('.schedule_write').click(function (e) {
		e.preventDefault();
		e.stopPropagation(); // 이벤트 전파 차단
		
		// 다른 모달 닫기
		$('.modal_schedule_write, .modal_sd_detail, .modal_schedule_modify').hide();
	
		// 현재 모달 열기
		$('.modal_schedule_write').show();
		$('#modal_1').css({
		  	top: '80px',
		  	left: '50%',
		  	transform: 'translateX(-50%)'
		});
    });

    // 일정 상세 모달 열기
    $('.scheList').click(function (e) {
	    e.preventDefault();
	    e.stopPropagation(); // 이벤트 전파 차단
		  
		const scheno = $(this).data('scheno'); // li에 data-scheno 속성 있어야 함

		$.ajax({
		    url: '/index/detail',
		    type: 'get',
		    data: { scheno },
		    success: function (data) {
		        showScheduleDetailModal(data); // ← 여기서 넘겨줌
		    },
		    error: function () {
		        alert("일정 정보를 불러오지 못했습니다.");
		    }
		});
	  

    	// 다른 모달 닫기
    	$('.modal_schedule_write, .modal_sd_detail, .modal_schedule_modify').hide();

    	// 현재 모달 열기
		$('.modal_sd_detail').show();
		$('#modal_2').css({
	        top: '80px',
	        left: '50%',
	        transform: 'translateX(-50%)'
    	});
    });
	
	// 수정하기 버튼 클릭 시 수정 모달 열기
	$('#btn_modify').click(function() {
	    const data = window.scheduleData; // 상세보기에서 저장해둔 데이터
	    
	    if (!data) {
	        alert('수정할 데이터가 없습니다.');
	        return;
	    }

	    $('#edit_scheno').val(data.scheno);
	    $('#edit_schetitle').val(data.schetitle);
	    $('#edit_sche_chk').val(data.sche_chk);
	    $('#edit_cal_chk').val(data.cal_chk);
	    $('#edit_startdate').val(data.startdate);
	    $('#edit_enddate').val(data.enddate);
	    $('#edit_starttime').val(data.starttime);
	    $('#edit_endtime').val(data.endtime);
	    $('#edit_wrtnm').val(data.wrtnm);
	    $('#edit_schecont').val(data.schecont);

	    $('.modal_sd_detail').hide();
	    $('.modal_schedule_modify').show();
	    $('#modal_3').css({
	        top: '80px',
	        left: '50%',
	        transform: 'translateX(-50%)'
	    });
	});
	
	
	// 일정 수정 처리
	$('#btn_update').click(function(e) {
		e.preventDefault();	// 기본 제출 동작 차단
		
		// 현재 수정폼 데이터 객체화
		const currentData = {
			schetitle: $('#edit_schetitle').val(),
			sche_chk: $('#edit_sche_chk').val(),
			cal_chk: $('#edit_cal_chk').val(),
			startdate: $('#edit_startdate').val(),
			enddate: $('#edit_enddate').val(),
			starttime: $('#edit_starttime').val(),
			endtime: $('#edit_endtime').val(),
			wrtnm: $('#edit_wrtnm').val(),
			schecont: $('#edit_schecont').val()
		};
		
		//기존 상세보기 저장 제이터 참조
		const originalData = window.scheduleData;
		
		//변경여부 확인 변수
		let isChanged = false;
		
		//새로 입력한 값과 원본 비교
		for(let key in currentData) {
			const currentVal = currentData[key] || '';					//현재입력값
			const originalVal = (originalData[key] || '').toString();	//원본 값 문자열 변환 후 비교
			
			if(currentVal != originalVal) {
				isChanged = true;
				break;
			}
		}
		
		if(!isChanged) {
			alert('수정된 내용이 없습니다.');
			return;
		}
		
		if(!currentData.schetitle || currentData.schetitle.trim() == ''){
			alert('제목은 필수항목 입니다.');
			$('edit_schetitle').focus();
			return;
		}
		
		const formData = $('#frm_modify').serialize();
		
		$.ajax({
			url: '/index/update',
			type: 'POST',
			data: formData,
			success: function (res) {
				if(res == '1') {
					alert('수정성공');	
					$('.modal_schedule_modify').hide();
					location.reload();
				} else {
					alert('수정실패');
				}
			},
			error: function() {
				alert('수정실패')
			}
		});
	});
	
	
	//일정 삭제
	$('#btn_delete').click(function() {
		if(!confirm('정말 삭제하시겠습니까?')) return;
		
		const scheno = window.scheduleData?.scheno;
		if(!scheno) {
			alert('삭제할 데이터가 없습니다.');
			return;
		}
		
		$.ajax({
			url: '/index/delete',
			type: 'POST',
			data: {scheno: scheno},
			success: function(res) {
				if(res == '1') {
					alert('삭제되었습니다.');
					location.reload();
				} else {
					alert('삭제 실패');
				}
			},
			error: function () {
				alert('서버오류!!');
			}
		});
	});
	


	// 모달 닫기
	$('.close_modal').click(function () {
    	$('.modal_schedule_write, .modal_sd_detail, .modal_schedule_modify').hide();
	});

	// 드래그 설정 (transform 제거)
	$('#modal_1, #modal_2, #modal_3').draggable({
    	handle: '.modal_header',
    	start: function () {
        	$(this).css('transform', 'none');
    	}
	});
  
  
  
	function showScheduleDetailModal(data) {
		
		window.scheduleData = data;
		
		const labelToKeyMap = {
		    "제목": "schetitle",
		    "구분": "sche_chk",
		    "시작일": "startdate",
		    "종료일": "enddate",
		    "시작시간": "starttime",
		    "종료시간": "endtime",
		    "작성자": "wrtnm",
		    "설명": "schecont",
		    "공개범위": "cal_chk"
		};
		
		const scheChkMap = {
		    1: "휴가",
		    2: "외근",
		    3: "출장"
		};
		
		const calChkMap = {
		    1: "개인",
		    2: "전체공유",
		    3: "팀공유"
		};
		
		$('#modal_2 .modal_contents .form_list').each(function () {
		    const label = $(this).find('label').text().trim();
		    const key = labelToKeyMap[label];
		    const $valueDiv = $(this).find('.value');
		
		    if (!key) return;
		
		    let value = data[key] ?? '-';
		
		    if (key === 'sche_chk') value = scheChkMap[data.sche_chk] || '-';
		    if (key === 'cal_chk') value = calChkMap[data.cal_chk] || '-';
		
		    $valueDiv.text(value);
		});
	}
});