const {defineStore} = Pinia

const initialState=()=>({
	list:[],
	curpage:1,
	totalpage:0,
	startPage:0,
	endPage:0
})

const useFoodStore=defineStore('food_store',{
	state: initialState,
	//Stroe안에 저장된 state를 가지고 새로운 값을 계산해서 반환하는 역할
	// 총액 계산, 페이지 번호 ... , 천단위 콤마 
	getters:{
		range:(state)=>{
			const arr=[]
			for(let i = state.startPage; i<= state.endPage; i++)
			{
				arr.push(i) //맨뒤에 값을 push
							//제거는 pop()
			}
			return arr;
		}
	},
	//사용자 요청 기능 => 서버 연동
	actions:{
		async foodListData(){
			const res = await api.get('/food/list_vue',{
				params:{
					page: this.curpage
				}
			})
			
			console.log(res.data) //Map에 있는 데이터 값 받기
			this.list = res.data.list
			this.curpage = res.data.curpage
			this.totalpage = res.data.totalpage
			this.startPage = res.data.startPage
			this.endPage = res.data.endPage
		},
		move(page){
			this.curpage = page
			this.foodListData()
		}
	}
})