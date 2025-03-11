let handleTokenExpiration=()=>{
    $.ajax({
        type: 'POST',
        url: '/refresh-token',
        contentType: 'application/json;charset=utf-8',//전송데이터 타입
        dataType: 'json',//서버에서 받을 데이터의 타입
        xhrFields: {
            withCredentials: true // 쿠키를 포함해서 요청을 보냄
        },
        success: (response)=>{
            localStorage.setItem('accessToken',response.token);
        },
        error: ()=>{
            alert('로그인이 필요합니다.다시 로그인 해주세요.')
            window.location.href="/member/login"
        }
    });
}

let checkToken=()=>{
    let token=localStorage.getItem('accessToken');
    if (token==null || token.trim()===''){
        //토큰 앞,뒤 공백 제거하여 토큰이 빈문자열일 경우에도 처리
        window.location.href='/member/login';
    }
}

let setupAjax=()=>{
    //모든 ajax요청에 Access Token을 포함
    $.ajax({

        //서버 요청전에 실행되는 콜백 함수.xhr(XML Http Request)은 웹브라우저에서 서버와 비동기적으로
        // 데이터를 주고받을 수 있게 해주는 자바스크립트 객체임
        beforeSend: (xhr)=>{
            let token=localStorage.getItem('accessToken');
            if(token){
                //요청 헤더에 'Authorization'키로 'bearer '+토큰값 을 추가.
                xhr.setRequestHeader('Authorization','bearer '+token)
            }
        }
    })
}

let getUserInfo = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',           // HTTP GET 요청을 보냄
            url: '/user/info',     // 요청을 보낼 엔드포인트 URL
            success: (response) => {
                resolve(response); // 성공 시, Promise를 resolve하여 데이터 반환
            },
            error: (xhr) => {       // 요청 실패 시 실행되는 코드
                console.log('xhr :: ', xhr); // 콘솔에 에러 출력
                if (xhr.status === 401) {    // HTTP 상태 코드가 401 (Unauthorized)일 경우
                    handleTokenExpiration(); // 인증 만료 처리 함수 실행
                } else {
                    reject(xhr);  // 그 외의 에러는 reject하여 호출한 쪽에서 처리하도록 함
                }
            }
        });
    });
}

