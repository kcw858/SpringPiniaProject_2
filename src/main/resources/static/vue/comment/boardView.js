const {createApp,onMounted,onUnmounted,ref} = Vue
		const {createPinia} = Pinia
		const commentApp = createApp({
			setup(){
				// store를 가지고온다
				const store = useBoardStore()
				// ref
				const msgRef = ref(null)
				console.log(store)
				//시작과 동시에 데이터 읽기
				onMounted(()=>{
					store.boardCommentListData(BOARDNO,SESSION_ID)
					store.connect(SESSION_ID)
				})
				onUnmounted(()=>{
					store.disConnection()
				})
				//HTML에서 사용이 가능하게 변수 설정
				return {
					store,
					msgRef
				}
			}
		})
		commentApp.use(createPinia())
		commentApp.mount("#comment")