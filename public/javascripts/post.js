$(function() {
    $('#submit').submit(function() {
        $.ajax({
            type: 'POST',
            url: '/post',
            data: {
                token: $('#token').val(),
                title: $('#title').val(),
                content: $('#content').val()
            }
        }).done(function(data) {
            $().toastmessage('showToast', {
                text: '記事投稿に成功しました！',
                position: 'top-center',
                type: 'success',
                stayTime: 3000,
                close: function() {
                    $('#link').html('<a href="' + data['entry_url'] + '">ページを確認する</a>');
                }
            });
        }).fail(function(data) {
            $().toastmessage('showToast', {
                text: '記事投稿に失敗しました...。',
                position: 'top-center',
                type: 'error',
                stayTime: 2000,
                close: function() {
                    console.debug('TODO: エラーメッセージをここで表示する。');
                }
            });
        }).always(function(data) {});

        return false;
    });
});
