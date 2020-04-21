package com.example.cermatitestapp.ui.main

import androidx.lifecycle.*
import com.example.cermatitestapp.api.model.GithubUser
import com.example.cermatitestapp.database.repositories.UserSearchResultRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    private val userSearchResultRepository: UserSearchResultRepository
) : ViewModel() {

    private val _loadingUserListEvent = MutableLiveData<Boolean>()
    val loadingUserListEvent: LiveData<Boolean> = _loadingUserListEvent
    private val _userSearchResults = userSearchResultRepository.userSearchResults
    val userSearchResults: LiveData<List<GithubUser>> = _userSearchResults
    // untuk saat ini kacangin dulu detail errornya. Pake boolean aja dulu.
    private val _userSearchFetchError = MutableLiveData<Boolean>()
    val userSearchFetchError: LiveData<Boolean> = _userSearchFetchError
    val isListEmpty: LiveData<Boolean> = Transformations.map(_userSearchResults){
        it.isNullOrEmpty() && mPage > 1
    }
    // for data filter and pagination
    var mPage = 1
    private set

    private val mKeyword = MutableLiveData<String?>()
    private val mKeywordObserver = Observer<String?> {
        // reset pagination
        mPage = 1
        // clear error flag
        _userSearchFetchError.postValue(false)
        // clear previous list
        _userSearchResults.postValue(listOf())
        // call API if keyword isn't empty
        if (!it.isNullOrBlank()) callAPI(mPage, it)

    }

    // for calling APIs
    private val disposables = CompositeDisposable()

    init {
        mKeyword.observeForever(mKeywordObserver)
    }

    private fun callAPI(page: Int, keyword: String) {
        val observable = userSearchResultRepository.fetchRemoteData(page, keyword)

        val disposable = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _loadingUserListEvent.postValue(true)
                _userSearchFetchError.postValue(false)
            }
            .doOnDispose {
                _loadingUserListEvent.postValue(false)
                _userSearchFetchError.postValue(false)
            }.subscribe({
                _loadingUserListEvent.postValue(false)
                if(it.isSuccessful){
                    val combinedList: List<GithubUser> = if(page ==1)
                        it.body()?.items ?: listOf()
                    else
                        _userSearchResults.value?.toTypedArray()?.let { it1 ->
                            listOf(*it1, *it.body()?.items?.toTypedArray() ?: listOf<GithubUser>().toTypedArray()) }
                            ?: listOf(*it.body()?.items?.toTypedArray() ?: listOf<GithubUser>().toTypedArray())
                    _userSearchResults.postValue(combinedList)

                    // todo kalau sempet: simpen hasil pencarian terakhir sbg cache untuk jaga2
                    //  kalau internet down jadi bisa dicari lagi hasil pencarian terakhir
                    userSearchResultRepository.convertAndInsert(it.body()?.items)
                    mPage = page + 1
//              } else _userSearchFetchError.postValue(it.errorBody().toString())
                } else _userSearchFetchError.postValue(true)
            }, {
                _loadingUserListEvent.postValue(false)
//              _userSearchFetchError.postValue(it.message.toString())
                _userSearchFetchError.postValue(true)
            })

        disposables.add(disposable)
    }

    override fun onCleared() {
        disposables.dispose()
        mKeyword.removeObserver(mKeywordObserver)
        super.onCleared()
    }

    fun setKeyword(keyword: String){
        mKeyword.postValue(keyword)
    }

    fun loadNextPage(){
        mKeyword.value?.let {
            callAPI(mPage, it)
        }
    }

    fun resetSearch() {
        setKeyword("")
    }


}
