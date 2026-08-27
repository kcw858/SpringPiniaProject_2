const { defineStore } = Pinia
const { nextTick } = Vue

//defineStore-> 새로운 store를 만드는 경우
const useChatStore = defineStore('chat', {

    state: () => ({
		// STOMP => 규칙을 설정해서 서버와 통신
        stomp: null,
		// 현재 접속한 사용자 목록 -> 로그인 요청을 하면 서버에서 전체 전송
        users: [],
		// 채팅메세지를 모아서 한번에 처리
        messages: [],
		//전체 채팅 메세지
        publicMessages: [],
		//1:1 채팅 메세지
        privateMessages: {},
		//현재 채팅방 (id_id는 1대1)
        currentRoom: 'public',
		//현재 로그인한 사용자 ${session.username}
        loginUser: '',
		//채팅창 변경시 => 전체 => 1대1 DOM
        chatBodyEl: null,
		//사용지 입력 메세지 => v-model="store.msg"
        msg: ''
    }),
	
	//서버 연결 => 데이터 변경
	//자체 데이터 변경 ==> HTML을 변경
	//------------------------------ Model
	//------------------------------ ViewModel
	//------------------------------ View (HTML) => mount
    actions: {
		//1:1방 생성 => hong_kim
        makeRoomId(user1, user2) {
            return [
                user1,
                user2
            ]
            .sort()
            .join('_')
        },
		
		//채팅방에서 상대방 선택
        getOtherUser(roomId) {

			//전체 채팅중이면 
            if (roomId === 'public') {
                return ''
            }

			//방이름을 사용자 분류
            const users =
                roomId.split('_')

			//로그인 사용자가 첫번째면 
			//두번째 사용자가 상대방
            return users[0] === this.loginUser
                ? users[1]
                : users[0]
        },

        changeRoom(user) {
			//현재 방이 전체 채팅이면 
            if (user === 'public') {

                this.currentRoom = 'public'
				//Topic이용 1대1 채팅
                this.messages =
                    this.publicMessages
            }
			//1대1 채팅
            else {
				//방을 생성
                const roomId =
                    this.makeRoomId(
                        this.loginUser,
                        user
                    )
				//현재 채팅창 변경
                this.currentRoom =
                    roomId

				//해당되는 방이 없는경우
                if (!this.privateMessages[roomId]) {
                    this.privateMessages[roomId] = []
                }
				//해당 방에만 네세지 전송
                this.messages =
                    this.privateMessages[roomId]
            }

            this.scrollToBottom()
        },

        connect() {

            const socket =
                new SockJS('/chat-ws')

            this.stomp =
                Stomp.over(socket)
			
			//STOMP 콘솔 로그 제거
            this.stomp.debug = null

			//서버와 실제 연동
            this.stomp.connect(
                {},

                () => {

                    console.log(
                        'WebSocket 연결 성공'
                    )

					//접속자 목록을 가지고 온다
					//subscribe() / sen()
					//  응답			요청
                    this.stomp.subscribe(
                        '/topic/users',

                        msg => {

                            const users =
                                JSON.parse(msg.body)

                            this.users =
                                users.filter(
                                    u =>
                                        u !== this.loginUser
                                )
                        }
                    )
					
					this.stomp.send(
						'/app/chat/join',{},
						JSON.stringify({})
					)

                    this.stomp.subscribe(
                        '/topic/chat',

                        msg => {

                            const m =
                                JSON.parse(msg.body)

                            this.publicMessages.push(m)

                            if (
                                this.currentRoom ===
                                'public'
                            ) {

                                this.messages =
                                    this.publicMessages

                                this.scrollToBottom()
                            }
                        }
                    )

                    this.stomp.subscribe(
                        '/user/queue/chat',

                        msg => {

                            const m =
                                JSON.parse(msg.body)

                            const roomId =
                                this.makeRoomId(
                                    m.sender,
                                    m.receiver
                                )

                            if (
                                !this.privateMessages[
                                    roomId
                                ]
                            ) {

                                this.privateMessages[
                                    roomId
                                ] = []
                            }

                            this.privateMessages[
                                roomId
                            ].push(m)

                            if (
                                this.currentRoom ===
                                roomId
                            ) {

                                this.messages =
                                    this.privateMessages[
                                        roomId
                                    ]

                                this.scrollToBottom()
                            }
                        }
                    )
					//종료시 사용
                    this.stomp.subscribe(
                        '/user/queue/force-disconnect',

                        () => {

                            alert(
                                '중복 로그인으로 로그아웃되었습니다.'
                            )

                            location.href =
                                '/logout'
                        }
                    )
                },

                error => {

                    console.error(
                        'WebSocket 연결 실패',
                        error
                    )
                }
            )
        },
	
		//스크롤바를 맨 마지막으로 내린다
        async scrollToBottom() {

            await nextTick()

            if (this.chatBodyEl) {

                this.chatBodyEl.scrollTop =
                    this.chatBodyEl.scrollHeight
            }
        },

        sendPublic(message) {

            this.stomp.send(
                '/app/chat/public',
                {},
                JSON.stringify({
                    message: message
                })
            )
        },

        sendPrivate(to, message) {

            this.stomp.send(
                '/app/chat/private',
                {},
                JSON.stringify({
                    receiver: to,
                    message: message
                })
            )
        },

        send() {

            if (!this.msg.trim()) {
                return
            }

            if (
                this.currentRoom ===
                'public'
            ) {

                this.sendPublic(
                    this.msg
                )
            }

            else {

                const users =
                    this.currentRoom.split('_')

                const receiver =
                    users[0] === this.loginUser
                        ? users[1]
                        : users[0]

                this.sendPrivate(
                    receiver,
                    this.msg
                )
            }

            this.msg = ''
        }
    }
})