// 원격 스트림 요소를 선택하는 코드 (현재 주석 처리됨)
// let remoteStreamElement = document.querySelector('#remoteStream');

// 로컬 스트림 요소를 선택
let localStreamElement = document.querySelector('#localStream');

// 임의의 키를 생성 이 키는 36진수로 변환된 무작위 문자열의 일부
const myKey = Math.random().toString(36).substring(2, 11);

// 피어 연결 객체를 저장할 맵을 초기화
let pcListMap = new Map();

// 방 ID를 저장할 변수
let roomId;

// 다른 사용자 키 목록을 저장할 배열
let otherKeyList = [];

// 로컬 스트림을 저장할 변수를 선언
let localStream = undefined;

// 카메라와 마이크 스트림을 시작하는 비동기 함수
const startCam = async () => {
    // navigator.mediaDevices 객체가 존재하는지 확인
    if (navigator.mediaDevices !== undefined) {
        // 카메라와 마이크 스트림을 요청
        await navigator.mediaDevices.getUserMedia({ audio: true, video: true })
            .then(async (stream) => {
                // 스트림을 찾기성공
                console.log('Stream 찾기 성공');
                
                // 웹캠과 마이크의 스트림 정보를 글로벌 변수로 저장
                localStream = stream;

                // 기본적으로 마이크를 활성화
                stream.getAudioTracks()[0].enabled = true;

                // 로컬 스트림 요소에 스트림을 설정하여 화면에 표시
                localStreamElement.srcObject = localStream;
console.log('흐름 찾기 성공1');
                // 로컬 스트림이 사용 가능해진 후에 연결을 설정
            }).catch(error => {
                // 미디어 장치에 접근하는 동안 오류가 발생하면 콘솔에 오류를 출력
                console.error("Error accessing media devices:", error);
            });
    }
}
    
 // 소켓 연결
 const connectSocket = async () =>{
	 console.log('흐름 찾기 성공2');
     const socket = new SockJS('/signaling');
     //const socket = new SockJS('https://172.30.1.80:8080/signaling');
     stompClient = Stomp.over(socket);
     stompClient.debug = null;
    
     stompClient.connect({}, function () {
         console.log('webRTC 연결성공');
            
 				//iceCandidate peer 교환을 위한 subscribe
         stompClient.subscribe(`/topic/peer/iceCandidate/${myKey}/${roomId}`, candidate => {
             const key = JSON.parse(candidate.body).key
             const message = JSON.parse(candidate.body).body;
    
 						// 해당 key에 해당되는 peer 에 받은 정보를 addIceCandidate 해준다.
             pcListMap.get(key).addIceCandidate(new RTCIceCandidate({candidate:message.candidate,sdpMLineIndex:message.sdpMLineIndex,sdpMid:message.sdpMid}));
    console.log('흐름 찾기 성공3', key);
         });
    				
 				//offer peer 교환을 위한 subscribe
         stompClient.subscribe(`/topic/peer/offer/${myKey}/${roomId}`, offer => {
             const key = JSON.parse(offer.body).key;
             const message = JSON.parse(offer.body).body;
    						
 						// 해당 key에 새로운 peerConnection 를 생성해준후 pcListMap 에 저장해준다.
             pcListMap.set(key,createPeerConnection(key));
 						// 생성한 peer 에 offer정보를 setRemoteDescription 해준다.
             pcListMap.get(key).setRemoteDescription(new RTCSessionDescription({type:message.type,sdp:message.sdp}));
             //sendAnswer 함수를 호출해준다.
 						sendAnswer(pcListMap.get(key), key);
    
         });
    				
 		//answer peer 교환을 위한 subscribe
         stompClient.subscribe(`/topic/peer/answer/${myKey}/${roomId}`, answer =>{
             const key = JSON.parse(answer.body).key;
             const message = JSON.parse(answer.body).body;
    						
 			// 해당 key에 해당되는 Peer 에 받은 정보를 setRemoteDescription 해준다.
             pcListMap.get(key).setRemoteDescription(new RTCSessionDescription(message));
    
         });
    				
 			  //key를 보내라는 신호를 받은 subscribe
         stompClient.subscribe(`/topic/call/key`, message =>{
 						//자신의 key를 보내는 send
             stompClient.send(`/app/send/key`, {}, JSON.stringify(myKey));
    
         });
    				
 				//상대방의 key를 받는 subscribe
         stompClient.subscribe(`/topic/send/key`, message => {
             const key = JSON.parse(message.body);
    						
 						//만약 중복되는 키가 ohterKeyList에 있는지 확인하고 없다면 추가해준다.
             if(myKey !== key && otherKeyList.find((mapKey) => mapKey === myKey) === undefined){
                 otherKeyList.push(key);
             }
         });
    
     });
 }
    
 let onTrack = (event, otherKey) => {
    
     if(document.getElementById(`${otherKey}`) === null){
         const video =  document.createElement('video');
    
         video.autoplay = true;
         video.controls = true;
         video.id = otherKey;
         video.srcObject = event.streams[0];
    
         document.getElementById('remoteStreamDiv').appendChild(video);
     }
    
     //
     // remoteStreamElement.srcObject = event.streams[0];
     // remoteStreamElement.play();
 };
    
 const createPeerConnection = (otherKey) =>{
     const pc = new RTCPeerConnection();
     try {
         pc.addEventListener('icecandidate', (event) =>{
             onIceCandidate(event, otherKey);
         });
         pc.addEventListener('track', (event) =>{
             onTrack(event, otherKey);
         });
         if(localStream !== undefined){
             localStream.getTracks().forEach(track => {
                 pc.addTrack(track, localStream);
             });
         }
    
         console.log('PeerConnection created');
     } catch (error) {
         console.error('PeerConnection failed: ', error);
     }
     return pc;
 }
    
 let onIceCandidate = (event, otherKey) => {
     if (event.candidate) {
         console.log('ICE candidate');
         stompClient.send(`/app/peer/iceCandidate/${otherKey}/${roomId}`,{}, JSON.stringify({
             key : myKey,
             body : event.candidate
         }));
     }
 };
    
 let sendOffer = (pc ,otherKey) => {
     pc.createOffer().then(offer =>{
         setLocalAndSendMessage(pc, offer);
         stompClient.send(`/app/peer/offer/${otherKey}/${roomId}`, {}, JSON.stringify({
             key : myKey,
             body : offer
         }));
         console.log('Send offer');
     });
 };
    
 let sendAnswer = (pc,otherKey) => {
     pc.createAnswer().then( answer => {
         setLocalAndSendMessage(pc ,answer);
         stompClient.send(`/app/peer/answer/${otherKey}/${roomId}`, {}, JSON.stringify({
             key : myKey,
             body : answer
         }));
         console.log('Send answer');
     });
 };
    
 const setLocalAndSendMessage = (pc ,sessionDescription) =>{
     pc.setLocalDescription(sessionDescription);
 }
    
 //룸 번호 입력 후 캠 + 웹소켓 실행
 document.querySelector('#enterRoomBtn').addEventListener('click', async () =>{
     await startCam();
    
     if(localStream !== undefined){
         document.querySelector('#localStream').style.display = 'block';
         document.querySelector('#startSteamBtn').style.display = '';
     }
     roomId = document.querySelector('#roomIdInput').value;
     document.querySelector('#roomIdInput').disabled = true;
     document.querySelector('#enterRoomBtn').disabled = true;
    
     await connectSocket();
 });
    
 // 스트림 버튼 클릭시 , 다른 웹 key들 웹소켓을 가져 온뒤에 offer -> answer -> iceCandidate 통신
 // peer 커넥션은 pcListMap 으로 저장
 document.querySelector('#startSteamBtn').addEventListener('click', async () =>{
     await stompClient.send(`/app/call/key`, {}, {});
    
     setTimeout(() =>{
    
         otherKeyList.map((key) =>{
             if(!pcListMap.has(key)){
                 pcListMap.set(key, createPeerConnection(key));
                 sendOffer(pcListMap.get(key),key);
             }
    
         });
    
     },1000);
 });